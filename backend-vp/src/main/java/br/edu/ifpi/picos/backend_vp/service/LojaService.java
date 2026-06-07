package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.dto.LojaRequestDTO;
import br.edu.ifpi.picos.backend_vp.dto.LojaResponseDTO;
import br.edu.ifpi.picos.backend_vp.model.Loja;
import br.edu.ifpi.picos.backend_vp.repository.LojaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LojaService {

    @Autowired
    private LojaRepository lojaRepository;

    public LojaResponseDTO criarLoja(LojaRequestDTO dto) {
        Loja novaLoja = new Loja();
        novaLoja.setNome(dto.nome());
        novaLoja.setEndereco(dto.endereco());
        
        String pinAleatorio = java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        novaLoja.setPin("VP-" + pinAleatorio); // Ex: VP-A83F92
        
        Loja lojaSalva = lojaRepository.save(novaLoja);
        return LojaResponseDTO.fromEntity(lojaSalva);
    }

    public List<LojaResponseDTO> listarTodas() {
        return lojaRepository.findAll()
                .stream()
                .map(LojaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public LojaResponseDTO buscarPorId(Long id) {
        Loja loja = lojaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Erro: Loja não encontrada!"));
        return LojaResponseDTO.fromEntity(loja);
    }
}