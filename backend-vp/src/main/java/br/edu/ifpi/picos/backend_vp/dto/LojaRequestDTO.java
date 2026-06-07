package br.edu.ifpi.picos.backend_vp.dto;

import jakarta.validation.constraints.NotBlank;

public record LojaRequestDTO(
        
        @NotBlank(message = "O nome da loja é obrigatório!")
        String nome,
        
        @NotBlank(message = "O endereço da loja é obrigatório!")
        String endereco
) {}