package br.edu.ifpi.picos.backend_vp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequestDTO(
        
        @NotBlank(message = "O nome é obrigatório!")
        String nome,
        
        @NotBlank(message = "O e-mail é obrigatório!")
        @Email(message = "Formato de e-mail inválido!")
        String email,
        
        @NotBlank(message = "A senha é obrigatória!")
        String senha
) {}