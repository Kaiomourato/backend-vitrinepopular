package br.edu.ifpi.picos.backend_vp.dto;

import java.math.BigDecimal;

public record OfertaRequestDTO(
        String produtoNome,
        BigDecimal preco,
        Long usuarioId,
        Long lojaId,
        Long categoriaId
) {
}