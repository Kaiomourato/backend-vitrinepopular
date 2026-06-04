package br.edu.ifpi.picos.backend_vp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "lojas")
public class Loja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String endereco;
    
    private String whatsapp;

    @Column(name = "is_parceira")
    private Boolean isParceira = false;

    // Relacionamento: Várias lojas podem ter o mesmo dono (se ele tiver filiais)
    @ManyToOne
    @JoinColumn(name = "dono_id")
    private Usuario dono;

    public Loja() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }
    public Boolean getIsParceira() { return isParceira; }
    public void setIsParceira(Boolean isParceira) { this.isParceira = isParceira; }
    public Usuario getDono() { return dono; }
    public void setDono(Usuario dono) { this.dono = dono; }
}