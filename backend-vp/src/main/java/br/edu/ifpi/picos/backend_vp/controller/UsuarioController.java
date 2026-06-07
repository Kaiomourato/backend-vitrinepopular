package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.UsuarioLoginDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.UsuarioResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Usuario;
import br.edu.ifpi.picos.backend_vp.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
@Tag(name = "Usuários", description = "Rotas para registo e autenticação na plataforma")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registrar")
    @Operation(summary = "Criar conta", description = "Regista um novo utilizador na base de dados")
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRequestDTO dto) {
        UsuarioResponseDTO resposta = usuarioService.registrar(dto);
        // [CORREÇÃO] 201 Created na criação de recurso
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica o utilizador e devolve o Token JWT")
    public br.edu.ifpi.picos.backend_vp.dto.LoginResponseDTO login(
            @RequestBody @Valid UsuarioLoginDTO dto) {
        return usuarioService.login(dto);
    }

    // [NOVO] Retorna o perfil do usuário autenticado a partir do token JWT.
    // O frontend usa essa rota logo após o login para saber nome, email,
    // perfil (LOJISTA/COLABORADOR) e ID — sem precisar armazenar esses dados
    // manualmente no momento do login.
    @GetMapping("/me")
    @Operation(
        summary = "Meu perfil",
        description = "Retorna os dados do usuário autenticado com base no token JWT",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public UsuarioResponseDTO meuPerfil() {
        Usuario usuarioAutenticado = (Usuario) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // Chama o service para garantir dados frescos do banco
        return usuarioService.buscarPorId(usuarioAutenticado.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Perfil público", description = "Busca os dados públicos de um utilizador específico pelo ID")
    public UsuarioResponseDTO buscarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }
}