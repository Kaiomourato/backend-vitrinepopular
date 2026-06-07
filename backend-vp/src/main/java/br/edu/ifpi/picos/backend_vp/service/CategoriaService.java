package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.CategoriaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.CategoriaResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Categoria;
import br.edu.ifpi.picos.backend_vp.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public CategoriaResponseDTO criarCategoria(CategoriaRequestDTO dto) {
        Categoria novaCategoria = new Categoria();
        novaCategoria.setNome(dto.nome());
        
        Categoria categoriaSalva = categoriaRepository.save(novaCategoria);
        return CategoriaResponseDTO.fromEntity(categoriaSalva);
    }

    public List<CategoriaResponseDTO> listarTodas() {
        return categoriaRepository.findAll()
                .stream()
                .map(CategoriaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CategoriaResponseDTO buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Categoria não encontrada!"));
        return CategoriaResponseDTO.fromEntity(categoria);
    }
}