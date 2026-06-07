package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.LojaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.LojaResponseDTO;
import br.edu.ifpi.picos.backend_vp.service.LojaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lojas")
@CrossOrigin(origins = "*")
@Tag(name = "Lojas", description = "Rotas para gerenciamento dos estabelecimentos locais")
public class LojaController {

    @Autowired
    private LojaService lojaService;

    @PostMapping
    @Operation(summary = "Cadastrar Loja", description = "Cria um novo estabelecimento na base de dados")
    public LojaResponseDTO criarLoja(@Valid @RequestBody LojaRequestDTO dto) {
        return lojaService.criarLoja(dto);
    }

    @GetMapping
    @Operation(summary = "Listar Lojas", description = "Traz todas as lojas cadastradas para os menus de seleção")
    public List<LojaResponseDTO> listarTodas() {
        return lojaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da Loja", description = "Busca os dados de uma loja específica")
    public LojaResponseDTO buscarPorId(@PathVariable Long id) {
        return lojaService.buscarPorId(id);
    }
}