package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import br.edu.ifpi.picos.backend_vp.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LojaRepository lojaRepository;

    public UsuarioResponseDTO registrar(UsuarioRequestDTO dto) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findByEmail(dto.email());
        if (usuarioExistente.isPresent()) {
            throw new RuntimeException("Erro: Este e-mail já está em uso!");
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(dto.nome());
        novoUsuario.setEmail(dto.email());
        novoUsuario.setSenha(dto.senha()); 
        
        // 1. Define o Perfil
        if (dto.perfil() != null) {
            novoUsuario.setPerfil(dto.perfil());
        } else {
            novoUsuario.setPerfil(PerfilUsuario.COLABORADOR);
        }

        // 2. VALIDAÇÃO DE SEGURANÇA DO LOJISTA
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

    public UsuarioResponseDTO login(UsuarioLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Erro: Utilizador não encontrado!"));

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