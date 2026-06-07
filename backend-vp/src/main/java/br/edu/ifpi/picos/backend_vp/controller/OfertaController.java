package br.edu.ifpi.picos.backend_vp.controller;

import br.edu.ifpi.picos.backend_vp.dto.OfertaResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import br.edu.ifpi.picos.backend_vp.repository.OfertaRepository;
import br.edu.ifpi.picos.backend_vp.service.OfertaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import br.edu.ifpi.picos.backend_vp.dto.OfertaRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private OfertaService ofertaService;

    @GetMapping
    public Page<OfertaResponseDTO> listarOfertasAtivas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Oferta> ofertasPage = ofertaRepository.findByStatus(StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public OfertaResponseDTO criarOferta(
            @RequestParam("dados") String dadosJson,
            @RequestParam("imagem") org.springframework.web.multipart.MultipartFile imagem) {
            
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            OfertaRequestDTO dto = objectMapper.readValue(dadosJson, OfertaRequestDTO.class);
            
            return ofertaService.criarOfertaSegura(dto, imagem);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao ler os dados da oferta: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/votar-acabou")
    public Oferta votarAcabou(@PathVariable Long id) {
        return ofertaService.registrarVotoAcabou(id);
    }
}