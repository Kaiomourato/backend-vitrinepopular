package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.CategoriaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.CategoriaResponseDTO;
import br.edu.ifpi.picos.backend_vp.service.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
@Tag(name = "Categorias", description = "Rotas para organização e filtragem de ofertas")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping
    @Operation(summary = "Cadastrar Categoria", description = "Cria uma nova categoria na base de dados")
    public CategoriaResponseDTO criarCategoria(@Valid @RequestBody CategoriaRequestDTO dto) {
        return categoriaService.criarCategoria(dto);
    }

    @GetMapping
    @Operation(summary = "Listar Categorias", description = "Traz todas as categorias para os filtros do app")
    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaService.listarTodas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da Categoria", description = "Busca os dados de uma categoria específica")
    public CategoriaResponseDTO buscarPorId(@PathVariable Long id) {
        return categoriaService.buscarPorId(id);
    }
}