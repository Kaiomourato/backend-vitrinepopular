package br.edu.ifpi.picos.backend_vp.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "favoritos")
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "data_favoritado", nullable = false)
    private LocalDateTime dataFavoritado = LocalDateTime.now();

    // Relações (Chaves Estrangeiras)
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private Oferta oferta;

    public Favorito() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getDataFavoritado() { return dataFavoritado; }
    public void setDataFavoritado(LocalDateTime dataFavoritado) { this.dataFavoritado = dataFavoritado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Oferta getOferta() { return oferta; }
    public void setOferta(Oferta oferta) { this.oferta = oferta; }
}