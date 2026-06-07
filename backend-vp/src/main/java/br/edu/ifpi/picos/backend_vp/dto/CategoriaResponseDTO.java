package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Categoria;

public record CategoriaResponseDTO(Long id, String nome) {
    
    public static CategoriaResponseDTO fromEntity(Categoria categoria) {
        return new CategoriaResponseDTO(
                categoria.getId(),
                categoria.getNome()
        );
    }
}