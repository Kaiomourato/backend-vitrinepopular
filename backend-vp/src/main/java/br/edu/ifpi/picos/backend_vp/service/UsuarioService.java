package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.LoginResponseDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.dto.VincularLojaDTO;
import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import br.edu.ifpi.picos.backend_vp.repository.UsuarioRepository;
import br.edu.ifpi.picos.backend_vp.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LojaRepository lojaRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.email());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Erro: Este e-mail já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(passwordEncoder.encode(dto.senha()));

        if (dto.perfil() != null) {
            novoUsuario.setPerfil(dto.perfil());
        } else {
            novoUsuario.setPerfil(PerfilUsuario.COLABORADOR);
        }

        if (novoUsuario.getPerfil() == PerfilUsuario.LOJISTA) {
            if (dto.lojaId() == null) {
                throw new RuntimeException("Erro: Lojistas precisam informar o ID da loja!");
            }
            if (dto.pinLoja() == null || dto.pinLoja().isBlank()) {
                throw new RuntimeException("Erro: O código PIN da loja é obrigatório para contas de lojistas!");
            }

            Loja lojaEncontrada = lojaRepository.findById(dto.lojaId())
                    .orElseThrow(() -> new RuntimeException("Erro: Loja não encontrada com o ID informado!"));

            if (!lojaEncontrada.getPin().equals(dto.pinLoja())) {
                throw new RuntimeException("Erro de Segurança: O Código PIN informado está incorreto para esta loja!");
            }

            novoUsuario.setLoja(lojaEncontrada);
        } else if (dto.lojaId() != null) {
            novoUsuario.setLoja(null);
        }

        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return UsuarioResponseDTO.fromEntity(usuarioSalvo);
    }

    public LoginResponseDTO login(UsuarioLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Erro: E-mail ou senha inválidos!"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RuntimeException("Erro: E-mail ou senha inválidos!");
        }

        String tokenJwt = tokenService.gerarToken(usuario);
        return new LoginResponseDTO(UsuarioResponseDTO.fromEntity(usuario), tokenJwt);
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }

    // Novo método: vincula um usuário já existente a uma loja pelo PIN
    public UsuarioResponseDTO vincularLoja(Usuario usuarioLogado, VincularLojaDTO dto) {
        // Busca o usuário atualizado do banco (o objeto do SecurityContext pode estar desatualizado)
        Usuario usuario = usuarioRepository.findById(usuarioLogado.getId())
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));

        if (usuario.getPerfil() == PerfilUsuario.LOJISTA && usuario.getLoja() != null) {
            throw new RuntimeException("Erro: Este utilizador já está vinculado a uma loja!");
        }

        Loja loja = lojaRepository.findById(dto.lojaId())
                .orElseThrow(() -> new RuntimeException("Erro: Loja não encontrada!"));

        if (!loja.getPin().equals(dto.pinLoja())) {
            throw new RuntimeException("Erro de Segurança: PIN incorreto para esta loja!");
        }

        usuario.setPerfil(PerfilUsuario.LOJISTA);
        usuario.setLoja(loja);

        Usuario atualizado = usuarioRepository.save(usuario);
        return UsuarioResponseDTO.fromEntity(atualizado);
    }
}