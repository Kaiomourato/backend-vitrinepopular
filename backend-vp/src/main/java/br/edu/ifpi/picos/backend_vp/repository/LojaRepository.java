package br.edu.ifpi.picos.backend_vp.repository;

import br.edu.ifpi.picos.backend_vp.model.Loja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LojaRepository extends JpaRepository<Loja, Long> {
}