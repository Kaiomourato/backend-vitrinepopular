package br.edu.ifpi.picos.backend_vp.dto;

import java.math.BigDecimal;

public record OfertaRequestDTO(
        String produtoNome,
        BigDecimal preco,
        String imagemUrl,
        Long usuarioId,
        Long lojaId,
        Long categoriaId
) {
}