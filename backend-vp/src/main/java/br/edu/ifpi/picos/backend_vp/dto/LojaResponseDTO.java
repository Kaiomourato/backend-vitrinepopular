package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Loja;

public record LojaResponseDTO(Long id, String nome, String endereco) {
    
    public static LojaResponseDTO fromEntity(Loja loja) {
        return new LojaResponseDTO(
                loja.getId(),
                loja.getNome(),
                loja.getEndereco()
        );
    }
}