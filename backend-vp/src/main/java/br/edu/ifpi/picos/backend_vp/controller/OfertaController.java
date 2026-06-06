package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import br.edu.ifpi.picos.backend_vp.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @GetMapping
    public List<Oferta> listarOfertasAtivas() {
        return ofertaRepository.findByStatus(StatusOferta.ATIVA);
    }

    @PostMapping
    public Oferta criarOferta(@RequestBody Oferta novaOferta) {
        return ofertaRepository.save(novaOferta);
    }
}