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

    // Exclui oferta se tiver mais de 3 votos "Acabou"
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


    // Verifica ofertas ativas a cada hora e expira as que foram postadas há mais de 48 horas
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


    // Criar Oferta
    public OfertaResponseDTO criarOfertaSegura(OfertaRequestDTO dto, org.springframework.web.multipart.MultipartFile imagem) {
        
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new RuntimeException("Erro: Usuário publicador não encontrado!"));
        

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
        novaOferta.setPreco(dto.preco());
        novaOferta.setUsuario(usuario);
        novaOferta.setLoja(loja);
        novaOferta.setCategoria(categoria);
        novaOferta.setImagemUrl(urlDaImagem); 

        Oferta salva = ofertaRepository.save(novaOferta);
        return OfertaResponseDTO.fromEntity(salva);
    }

    public OfertaResponseDTO buscarPorId(Long id) {
        Oferta oferta = ofertaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Oferta não encontrada no sistema!"));
        return OfertaResponseDTO.fromEntity(oferta);
    }
}