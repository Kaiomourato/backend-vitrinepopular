package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.dto.VincularLojaDTO;
import br.edu.ifpi.picos.backend_vp.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuários", description = "Rotas para registo e autenticação na plataforma")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    @Operation(summary = "Criar Conta", description = "Regista um novo utilizador na base de dados")
    public UsuarioResponseDTO registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        return usuarioService.registrar(dto);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer Login", description = "Autentica o utilizador e devolve o Token JWT")
    public br.edu.ifpi.picos.backend_vp.dto.LoginResponseDTO login(
            @RequestBody @Valid UsuarioLoginDTO dto) {
        return usuarioService.login(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil do Utilizador", description = "Busca os dados públicos de um utilizador específico")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    @PatchMapping("/vincular-loja")
    @Operation(
        summary = "Vincular à loja",
        description = "Atualiza o perfil do utilizador autenticado para LOJISTA, vinculando-o à loja pelo PIN.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public UsuarioResponseDTO vincularLoja(
            @AuthenticationPrincipal br.edu.ifpi.picos.backend_vp.model.Usuario usuarioLogado,
            @Valid @RequestBody VincularLojaDTO dto) {
        return usuarioService.vincularLoja(usuarioLogado, dto);
    }
}