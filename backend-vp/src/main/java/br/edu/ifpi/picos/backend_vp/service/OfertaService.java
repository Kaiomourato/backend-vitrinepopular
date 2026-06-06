package br.edu.ifpi.picos.backend_vp.service;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import br.edu.ifpi.picos.backend_vp.repository.OfertaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository ofertaRepository;

    // Exclui oferta se tiver mais de 3 votos "Acabou"
    private static final int LIMITE_VOTOS_ACABOU = 3;

    public Oferta registrarVotoAcabou(Long idOferta) {
        Optional<Oferta> ofertaOpt = ofertaRepository.findById(idOferta);
        
        if (ofertaOpt.isPresent()) {
            Oferta oferta = ofertaOpt.get();
    
            oferta.setVotosAcabou(oferta.getVotosAcabou() + 1);
            
            if (oferta.getVotosAcabou() >= LIMITE_VOTOS_ACABOU) {
                oferta.setStatus(StatusOferta.REMOVIDA);
            }
            
            return ofertaRepository.save(oferta);
        }
        throw new RuntimeException("Oferta não encontrada!");
    }
}