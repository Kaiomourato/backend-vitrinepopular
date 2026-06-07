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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import java.util.Set;

@RestController
@RequestMapping("/api/ofertas")
@CrossOrigin(origins = "*")
@Tag(name = "Ofertas", description = "Rotas para publicação e consulta de ofertas e promoções")
public class OfertaController {

    @Autowired
    private OfertaRepository ofertaRepository;

    @Autowired
    private OfertaService ofertaService;

    @Autowired
    private Validator validator;

    // ─── ROTAS PÚBLICAS (sem token) ──────────────────────────────────────────

    @GetMapping
    @Operation(summary = "Feed de ofertas ativas", description = "Lista paginada de todas as ofertas ativas, ordenadas da mais recente para a mais antiga")
    public Page<OfertaResponseDTO> listarOfertasAtivas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPostagem"));
        Page<Oferta> ofertasPage = ofertaRepository.findByStatus(StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhes da oferta", description = "Busca uma oferta específica pelo ID")
    public OfertaResponseDTO buscarOfertaPorId(@PathVariable Long id) {
        return ofertaService.buscarPorId(id);
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Ofertas por categoria", description = "Lista as ofertas ativas de uma categoria com paginação")
    public Page<OfertaResponseDTO> listarOfertasPorCategoria(
            @PathVariable Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPostagem"));
        Page<Oferta> ofertasPage = ofertaRepository.findByCategoriaIdAndStatus(categoriaId, StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    // [NOVO] Listar ofertas de uma loja específica — essencial para a "página da loja"
    @GetMapping("/loja/{lojaId}")
    @Operation(summary = "Ofertas de uma loja", description = "Lista as ofertas ativas de uma loja específica com paginação")
    public Page<OfertaResponseDTO> listarOfertasPorLoja(
            @PathVariable Long lojaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPostagem"));
        Page<Oferta> ofertasPage = ofertaRepository.findByLojaIdAndStatus(lojaId, StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    // [NOVO] Busca por texto livre no nome do produto — essencial para a barra de busca
    @GetMapping("/busca")
    @Operation(summary = "Buscar ofertas por texto", description = "Busca ofertas ativas cujo nome de produto contenha o texto informado")
    public Page<OfertaResponseDTO> buscarPorTexto(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable paginacao = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataPostagem"));
        Page<Oferta> ofertasPage = ofertaRepository.findByProdutoNomeContainingIgnoreCaseAndStatus(q, StatusOferta.ATIVA, paginacao);
        return ofertasPage.map(OfertaResponseDTO::fromEntity);
    }

    @PatchMapping("/{id}/votar-acabou")
    @Operation(summary = "Votar 'Acabou'", description = "Registra um voto de que a oferta não está mais disponível")
    public Oferta votarAcabou(@PathVariable Long id) {
        return ofertaService.registrarVotoAcabou(id);
    }

    // ─── ROTAS PROTEGIDAS (exigem token JWT) ─────────────────────────────────

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Criar oferta",
        description = "Publica uma nova oferta. O usuário publicador é identificado automaticamente pelo token JWT.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<OfertaResponseDTO> criarOferta(
            @io.swagger.v3.oas.annotations.Parameter(description = "JSON com os dados da oferta (sem usuarioId)")
            @RequestParam("dados") String dadosJson,
            @RequestPart("imagem") MultipartFile imagem) {

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            OfertaRequestDTO dto = objectMapper.readValue(dadosJson, OfertaRequestDTO.class);

            Set<ConstraintViolation<OfertaRequestDTO>> erros = validator.validate(dto);
            if (!erros.isEmpty()) {
                throw new RuntimeException("Erro de Validação: " + erros.iterator().next().getMessage());
            }

            OfertaResponseDTO resposta = ofertaService.criarOfertaSegura(dto, imagem);
            // [CORREÇÃO] Retornar 201 Created ao invés de 200 OK na criação
            return ResponseEntity.status(HttpStatus.CREATED).body(resposta);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Erro: O formato do JSON está incorreto! Certifique-se de que usou aspas duplas nas chaves.");
        }
    }

    // [NOVO] Editar oferta — só o dono pode editar
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Editar oferta",
        description = "Atualiza nome, descrição, preço e/ou imagem de uma oferta. Só o criador da oferta pode editá-la.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public OfertaResponseDTO editarOferta(
            @PathVariable Long id,
            @RequestParam("dados") String dadosJson,
            @RequestPart(value = "imagem", required = false) MultipartFile imagem) {

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            OfertaRequestDTO dto = objectMapper.readValue(dadosJson, OfertaRequestDTO.class);
            return ofertaService.editarOferta(id, dto, imagem);

        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException("Erro: O formato do JSON está incorreto!");
        }
    }

    // [NOVO] Deletar oferta — só o dono pode deletar
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover oferta",
        description = "Remove permanentemente uma oferta. Só o criador da oferta pode removê-la.",
        security = @SecurityRequirement(name = "bearer-key")
    )
    public ResponseEntity<Void> deletarOferta(@PathVariable Long id) {
        ofertaService.deletarOferta(id);
        // 204 No Content é o padrão REST para deleção bem-sucedida
        return ResponseEntity.noContent().build();
    }
}