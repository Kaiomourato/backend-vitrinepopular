package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import br.edu.ifpi.picos.backend_vp.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.edu.ifpi.picos.backend_vp.model.Categoria;
import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.repository.CategoriaRepository;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import br.edu.ifpi.picos.backend_vp.repository.UsuarioRepository;
import br.edu.ifpi.picos.backend_vp.dto.OfertaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.OfertaResponseDTO;

// [CORREÇÃO 2] Imports para extrair o usuário autenticado do contexto de segurança
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ImagemService imagemService;

    private static final int LIMITE_VOTOS_ACABOU = 3;

    public Oferta registrarVotoAcabou(Long idOferta) {
        Optional<Oferta> ofertaOpt = ofertaRepository.findById(idOferta);

        if (ofertaOpt.isPresent()) {
            Oferta oferta = ofertaOpt.get();
            oferta.setVotosAcabou(oferta.getVotosAcabou() + 1);

            if (oferta.getVotosAcabou() >= LIMITE_VOTOS_ACABOU) {
                oferta.setStatus(StatusOferta.REMOVIDA);
            }

            return ofertaRepository.save(oferta);
        }
        throw new RuntimeException("Oferta não encontrada!");
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
    public void verificarEExpirarOfertas() {
        java.time.LocalDateTime tempoLimite = java.time.LocalDateTime.now().minusHours(48);
        java.util.List<Oferta> ofertasExpiradas = ofertaRepository.findByStatusAndDataPostagemBefore(StatusOferta.ATIVA, tempoLimite);

        if (!ofertasExpiradas.isEmpty()) {
            for (Oferta oferta : ofertasExpiradas) {
                oferta.setStatus(StatusOferta.EXPIRADA);
            }
            ofertaRepository.saveAll(ofertasExpiradas);
            System.out.println("O Sistema expirou " + ofertasExpiradas.size() + " ofertas automaticamente!");
        }
    }

    public OfertaResponseDTO criarOfertaSegura(OfertaRequestDTO dto, MultipartFile imagem) {

        // [CORREÇÃO 2] Extrair o usuário autenticado diretamente do token JWT,
        // sem mais confiar no usuarioId que vinha no body da requisição.
        // O SecurityFilter já populou o SecurityContextHolder com o objeto Usuario.
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Recarregar do banco para garantir dados frescos (loja vinculada, etc.)
        Usuario usuario = usuarioRepository.findById(usuarioAutenticado.getId())
                .orElseThrow(() -> new RuntimeException("Erro: Usuário autenticado não encontrado no banco!"));

        // Regra de negócio para LOJISTA permanece igual, mas agora com o usuário seguro
        if (usuario.getPerfil() == br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario.LOJISTA) {
            if (dto.lojaId() == null) {
                throw new RuntimeException("Erro de Negócio: Um Lojista precisa obrigatoriamente associar a oferta à sua própria loja!");
            }
            if (usuario.getLoja() == null || !usuario.getLoja().getId().equals(dto.lojaId())) {
                throw new RuntimeException("Erro de Segurança: Um Lojista não tem permissão para publicar ofertas em nome de outra loja!");
            }
        }

        Loja loja = lojaRepository.findById(dto.lojaId())
                .orElseThrow(() -> new RuntimeException("Erro: Loja não encontrada com o ID informado!"));
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new RuntimeException("Erro: Categoria não encontrada com o ID informado!"));

        String urlDaImagem = imagemService.fazerUpload(imagem);

        Oferta novaOferta = new Oferta();
        novaOferta.setProdutoNome(dto.produtoNome());
        novaOferta.setDescricao(dto.descricao()); // [NOVO] campo descricao
        novaOferta.setPreco(dto.preco());
        novaOferta.setUsuario(usuario); // usuário seguro, vindo do token
        novaOferta.setLoja(loja);
        novaOferta.setCategoria(categoria);
        novaOferta.setImagemUrl(urlDaImagem);

        Oferta salva = ofertaRepository.save(novaOferta);
        return OfertaResponseDTO.fromEntity(salva);
    }

    // [NOVO] Deletar oferta — só o dono pode deletar a sua
    public void deletarOferta(Long ofertaId) {
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Erro: Oferta não encontrada!"));

        // Garante que somente o criador da oferta pode deletá-la
        if (!oferta.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new RuntimeException("Erro de Segurança: Você não tem permissão para remover esta oferta!");
        }

        ofertaRepository.delete(oferta);
    }

    // [NOVO] Editar oferta (nome, descrição e/ou preço) — só o dono pode editar
    public OfertaResponseDTO editarOferta(Long ofertaId, OfertaRequestDTO dto, MultipartFile imagem) {
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Oferta oferta = ofertaRepository.findById(ofertaId)
                .orElseThrow(() -> new RuntimeException("Erro: Oferta não encontrada!"));

        if (!oferta.getUsuario().getId().equals(usuarioAutenticado.getId())) {
            throw new RuntimeException("Erro de Segurança: Você não tem permissão para editar esta oferta!");
        }

        // Atualiza apenas os campos que chegaram
        if (dto.produtoNome() != null && !dto.produtoNome().isBlank()) {
            oferta.setProdutoNome(dto.produtoNome());
        }
        if (dto.descricao() != null) {
            oferta.setDescricao(dto.descricao());
        }
        if (dto.preco() != null) {
            oferta.setPreco(dto.preco());
        }

        // Troca a imagem somente se uma nova for enviada
        if (imagem != null && !imagem.isEmpty()) {
            String novaUrl = imagemService.fazerUpload(imagem);
            oferta.setImagemUrl(novaUrl);
        }

        return OfertaResponseDTO.fromEntity(ofertaRepository.save(oferta));
    }

    public OfertaResponseDTO buscarPorId(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Oferta não encontrada no sistema!"));
        return OfertaResponseDTO.fromEntity(oferta);
    }
}