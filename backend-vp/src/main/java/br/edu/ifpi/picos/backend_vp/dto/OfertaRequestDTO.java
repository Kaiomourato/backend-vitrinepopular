package br.edu.ifpi.picos.backend_vp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OfertaRequestDTO(

        @NotBlank(message = "O nome do produto não pode ficar em branco!")
        String produtoNome,

        @NotNull(message = "O preço é obrigatório!")
        @Positive(message = "O preço deve ser maior que zero!")
        BigDecimal preco,

        @NotNull(message = "O ID do usuário é obrigatório!")
        Long usuarioId,
        
        @NotNull(message = "O ID da loja é obrigatório!")
        Long lojaId,

        @NotNull(message = "A categoria é obrigatória!")
        Long categoriaId
) {
}