package br.edu.ifpi.picos.backend_vp.repository;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {
    
    // Busca todas as ofertas filtrando pelo status
    List<Oferta> findByStatus(StatusOferta status);

    //Busca ofertas por status e data de postagem anterior a um limite
    List<Oferta> findByStatusAndDataPostagemBefore(StatusOferta status, LocalDateTime dataLimite);
}