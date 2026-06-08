package br.edu.ifpi.picos.backend_vp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VincularLojaDTO(
        @NotNull(message = "O ID da loja é obrigatório!")
        Long lojaId,

        @NotBlank(message = "O PIN da loja é obrigatório!")
        String pinLoja
) {}