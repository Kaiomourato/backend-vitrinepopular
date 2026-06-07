package br.edu.ifpi.picos.backend_vp.repository;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OfertaRepository extends JpaRepository<Oferta, Long> {

    // Feed principal — filtra por status
    Page<Oferta> findByStatus(StatusOferta status, Pageable pageable);

    // Job de expiração — busca ativas postadas antes de um limite de tempo
    List<Oferta> findByStatusAndDataPostagemBefore(StatusOferta status, LocalDateTime dataLimite);

    // Filtro por categoria (já existia)
    Page<Oferta> findByCategoriaIdAndStatus(Long categoriaId, StatusOferta status, Pageable pageable);

    // [NOVO] Filtro por loja — para a "página da loja" no frontend
    Page<Oferta> findByLojaIdAndStatus(Long lojaId, StatusOferta status, Pageable pageable);

    // [NOVO] Busca por texto no nome do produto — para a barra de busca
    // Spring Data JPA gera o SQL automaticamente a partir do nome do método
    Page<Oferta> findByProdutoNomeContainingIgnoreCaseAndStatus(String texto, StatusOferta status, Pageable pageable);
}