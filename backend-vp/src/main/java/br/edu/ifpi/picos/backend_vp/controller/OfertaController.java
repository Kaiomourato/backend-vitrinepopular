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
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
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
    private Validator validator; // Adicionado para validar o JSON manualmente

    @GetMapping
    public Page<OfertaResponseDTO> listarOfertasAtivas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Oferta> ofertasPage = ofertaRepository.findByStatus(StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    // ==========================================
    // A ESTRATÉGIA À PROVA DE BALAS (OBJECT MAPPER)
    // ==========================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Criar Oferta", description = "Publica uma oferta vinculando o ID do usuário e a foto do produto")
    public OfertaResponseDTO criarOferta(
            @io.swagger.v3.oas.annotations.Parameter(description = "Cole o JSON completo da oferta aqui") 
            @RequestParam("dados") String dadosJson,
            @RequestPart("imagem") MultipartFile imagem) {
            
        try {
            // 1. Lê o texto e transforma no Record
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            OfertaRequestDTO dto = objectMapper.readValue(dadosJson, OfertaRequestDTO.class);
            
            // 2. Valida as regras (NotBlank, NotNull)
            Set<ConstraintViolation<OfertaRequestDTO>> erros = validator.validate(dto);
            if (!erros.isEmpty()) {
                throw new RuntimeException("Erro de Validação: " + erros.iterator().next().getMessage());
            }

            // 3. Manda para a nossa regra de segurança que criámos hoje!
            return ofertaService.criarOfertaSegura(dto, imagem);
            
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Erro: O formato do JSON está incorreto! Certifique-se de que usou aspas duplas nas chaves.");
        }
    }

    @PatchMapping("/{id}/votar-acabou")
    public Oferta votarAcabou(@PathVariable Long id) {
        return ofertaService.registrarVotoAcabou(id);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da Oferta", description = "Busca uma oferta específica pelo seu ID")
    public OfertaResponseDTO buscarOfertaPorId(@PathVariable Long id) {
        return ofertaService.buscarPorId(id);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Filtrar por Categoria", description = "Lista as ofertas ativas de uma categoria específica (com paginação)")
    public Page<OfertaResponseDTO> listarOfertasPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Oferta> ofertasPage = ofertaRepository.findByCategoriaIdAndStatus(categoriaId, StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }
}