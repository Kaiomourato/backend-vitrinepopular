package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario;
import br.edu.ifpi.picos.backend_vp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
    
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.email());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Erro: Este e-mail já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(dto.senha());
        novoUsuario.setPerfil(PerfilUsuario.COLABORADOR);
        
        Usuario usuarioSalvo = usuarioRepository.save(novoUsuario);
        return UsuarioResponseDTO.fromEntity(usuarioSalvo);
    }

    // 2. Fazer Login
    public UsuarioResponseDTO login(UsuarioLoginDTO dto) {
        // Procura pelo e-mail
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));

        // Valida a senha
        if (!usuario.getSenha().equals(dto.senha())) {
            throw new RuntimeException("Erro: Senha incorreta!");
        }

        return UsuarioResponseDTO.fromEntity(usuario);
    }


    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));
        return UsuarioResponseDTO.fromEntity(usuario);
    }
}