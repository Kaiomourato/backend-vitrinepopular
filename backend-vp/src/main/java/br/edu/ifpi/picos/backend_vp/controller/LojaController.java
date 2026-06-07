package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.LojaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.LojaResponseDTO;
import br.edu.ifpi.picos.backend_vp.service.LojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lojas")
@CrossOrigin(origins = "*")
@Tag(name = "Lojas", description = "Rotas para gerenciamento dos estabelecimentos locais")
public class LojaController {

    @Autowired
    private LojaService lojaService;

    // [CORREÇÃO] POST agora exige token — protegido pelo SecurityConfig.
    // O Swagger exibe o cadeado para indicar que precisa de autenticação.
    @PostMapping
    @Operation(
        summary = "Cadastrar loja",
        description = "Cria um novo estabelecimento. Requer autenticação.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<LojaResponseDTO> criarLoja(@Valid @RequestBody LojaRequestDTO dto) {
        LojaResponseDTO resposta = lojaService.criarLoja(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping
    @Operation(summary = "Listar lojas", description = "Traz todas as lojas cadastradas")
    public List<LojaResponseDTO> listarTodas() {
        return lojaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da loja", description = "Busca os dados de uma loja específica")
    public LojaResponseDTO buscarPorId(@PathVariable Long id) {
        return lojaService.buscarPorId(id);
    }
}