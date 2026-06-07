package br.edu.ifpi.picos.backend_vp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OfertaRequestDTO(

        @NotBlank(message = "O nome do produto não pode ficar em branco!")
        @Size(max = 100, message = "O nome do produto deve ter no máximo 100 caracteres!")
        String produtoNome,

        // [NOVO] Campo de descrição para o lojista detalhar a promoção
        @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres!")
        String descricao,

        @NotNull(message = "O preço é obrigatório!")
        @Positive(message = "O preço deve ser maior que zero!")
        BigDecimal preco,

        // [CORREÇÃO 2] usuarioId REMOVIDO daqui.
        // O usuário será extraído do token JWT no controller,
        // eliminando a brecha de segurança onde qualquer um podia
        // postar em nome de outro usuário.

        @NotNull(message = "O ID da loja é obrigatório!")
        Long lojaId,

        @NotNull(message = "A categoria é obrigatória!")
        Long categoriaId
) {}