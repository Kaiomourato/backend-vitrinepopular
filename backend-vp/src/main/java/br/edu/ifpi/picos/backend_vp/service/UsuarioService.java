package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.LoginResponseDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import br.edu.ifpi.picos.backend_vp.repository.UsuarioRepository;
import br.edu.ifpi.picos.backend_vp.security.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // [CORREÇÃO 1] Import do encoder
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
    private PasswordEncoder passwordEncoder; // [CORREÇÃO 1] Injetar o Bean declarado no SecurityConfig

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.email());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Erro: Este e-mail já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());

        // [CORREÇÃO 1] Criptografar a senha ANTES de salvar no banco
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

        // [CORREÇÃO 1] Comparar a senha digitada com o hash BCrypt salvo no banco
        // Nunca mais .equals() em senha!
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            // Mensagem genérica para não revelar se o e-mail existe ou não
            throw new RuntimeException("Erro: E-mail ou senha inválidos!");
        }

        String tokenJwt = tokenService.gerarToken(usuario);

        return new LoginResponseDTO(
                UsuarioResponseDTO.fromEntity(usuario),
                tokenJwt
        );
    }

    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }
}