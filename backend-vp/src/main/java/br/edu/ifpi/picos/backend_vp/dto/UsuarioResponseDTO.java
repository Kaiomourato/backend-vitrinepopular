package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Usuario;

public record UsuarioResponseDTO(Long id, String nome, String email) {
    
    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}