package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/lojas")
@CrossOrigin(origins = "*")
public class LojaController {

    @Autowired
    private LojaRepository lojaRepository;

    @GetMapping
    public List<Loja> listarLojas() {
        return lojaRepository.findAll();
    }
}