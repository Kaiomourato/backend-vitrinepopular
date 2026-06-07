package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.model.enums.PerfilUsuario;

// [CORREÇÃO] Expor o perfil do usuário e a loja vinculada.
// Antes: só id, nome e email.
// Agora: o frontend sabe se a conta é LOJISTA ou COLABORADOR e,
// se for lojista, qual loja está vinculada — sem fazer chamadas extras.
public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        PerfilUsuario perfil,       // LOJISTA ou COLABORADOR
        LojaVinculadaDTO loja       // null se for COLABORADOR
) {

    // DTO interno leve — só o necessário para o frontend identificar a loja do lojista
    public record LojaVinculadaDTO(Long id, String nome) {}

    public static UsuarioResponseDTO fromEntity(Usuario usuario) {
        LojaVinculadaDTO lojaDTO = null;

        if (usuario.getLoja() != null) {
            lojaDTO = new LojaVinculadaDTO(
                    usuario.getLoja().getId(),
                    usuario.getLoja().getNome()
            );
        }

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                lojaDTO
        );
    }
}