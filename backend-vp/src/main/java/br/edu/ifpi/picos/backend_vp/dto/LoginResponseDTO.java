package br.edu.ifpi.picos.backend_vp.dto;

public record LoginResponseDTO(UsuarioResponseDTO perfil, String token) {}