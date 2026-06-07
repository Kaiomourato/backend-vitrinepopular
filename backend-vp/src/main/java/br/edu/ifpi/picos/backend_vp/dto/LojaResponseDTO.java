package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Loja;

public record LojaResponseDTO(Long id, String nome, String endereco, String pin) {
    
    public static LojaResponseDTO fromEntity(Loja loja) {
        return new LojaResponseDTO(
                loja.getId(),
                loja.getNome(),
                loja.getEndereco(),
                loja.getPin()
        );
    }
}