package br.edu.ifpi.picos.backend_vp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "metricas_cliques")
public class MetricaClique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_clique", nullable = false, length = 50)
    private String tipoClique; // Ex: "WHATSAPP", "MAPA"

    @Column(name = "data_clique", nullable = false)
    private LocalDateTime dataClique = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;

    public MetricaClique() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoClique() { return tipoClique; }
    public void setTipoClique(String tipoClique) { this.tipoClique = tipoClique; }
    public LocalDateTime getDataClique() { return dataClique; }
    public void setDataClique(LocalDateTime dataClique) { this.dataClique = dataClique; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
}