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

    
    private static final int LIMITE_VOTOS_ACABOU = 3;

    // Exclui oferta se tiver mais de 3 votos "Acabou"
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


    // Verifica ofertas ativas a cada hora e expira as que foram postadas há mais de 48 horas
    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 * * * *")
    public void verificarEExpirarOfertas() {

        java.time.LocalDateTime tempoLimite = java.time.LocalDateTime.now().minusHours(48);
        java.util.List<Oferta> ofertasExpiradas = ofertaRepository.findByStatusAndDataPostagemBefore(StatusOferta.ATIVA, tempoLimite);

        if (!ofertasExpiradas.isEmpty()) {
            for (Oferta oferta : ofertasExpiradas) {
                oferta.setStatus(StatusOferta.EXPIRADA);
            }
            ofertaRepository.saveAll(ofertasExpiradas);
            System.out.println("O Sistema expirou " + ofertasExpiradas.size() + " ofertas automaticamente!");
        }
    }
}