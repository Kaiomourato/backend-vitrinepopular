package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.model.Categoria;
import br.edu.ifpi.picos.backend_vp.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaRepository.findAll();
    }
}