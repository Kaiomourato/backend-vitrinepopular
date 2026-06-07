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

import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
public class OfertaController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private Validator validator;

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
            
            Set<ConstraintViolation<OfertaRequestDTO>> erros = validator.validate(dto);
            if (!erros.isEmpty()) {
                String mensagemErro = erros.iterator().next().getMessage();
                throw new IllegalArgumentException(mensagemErro);
            }

            return ofertaService.criarOfertaSegura(dto, imagem);
            
        } catch (Exception e) {
            throw new RuntimeException("Erro: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/votar-acabou")
    public Oferta votarAcabou(@PathVariable Long id) {
        return ofertaService.registrarVotoAcabou(id);
    }

    @GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Detalhes da Oferta", description = "Busca uma oferta específica pelo seu ID")
    public OfertaResponseDTO buscarOfertaPorId(@PathVariable Long id) {
        return ofertaService.buscarPorId(id);
    }

    @GetMapping("/categoria/{categoriaId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Filtrar por Categoria", description = "Lista as ofertas ativas de uma categoria específica (com paginação)")
    public Page<OfertaResponseDTO> listarOfertasPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Oferta> ofertasPage = ofertaRepository.findByCategoriaIdAndStatus(categoriaId, StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }
}