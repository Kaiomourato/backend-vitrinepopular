package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.OfertaResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import br.edu.ifpi.picos.backend_vp.repository.OfertaRepository;
import br.edu.ifpi.picos.backend_vp.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import br.edu.ifpi.picos.backend_vp.dto.OfertaRequestDTO;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private OfertaService ofertaService;

    @GetMapping
    public List<OfertaResponseDTO> listarOfertasAtivas() {
        List<Oferta> ofertas = ofertaRepository.findByStatus(StatusOferta.ATIVA);
        
        return ofertas.stream()
                .map(OfertaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping
    public OfertaResponseDTO criarOferta(@RequestBody OfertaRequestDTO dto) {
        return ofertaService.criarOfertaSegura(dto);
    }

    @PatchMapping("/{id}/votar-acabou")
    public Oferta votarAcabou(@PathVariable Long id) {
        return ofertaService.registrarVotoAcabou(id);
    }
}