package br.edu.ifpi.picos.backend_vp.model;

import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ofertas")
public class Oferta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "produto_nome", nullable = false, length = 100)
    private String produtoNome;

    // [NOVO] Campo de descrição livre para o lojista detalhar a promoção,
    // condições, prazo de validade etc.
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(name = "imagem_url", nullable = false)
    private String imagemUrl;

    @Column(name = "data_postagem")
    private LocalDateTime dataPostagem = LocalDateTime.now();

    @Column(name = "is_oficial")
    private Boolean isOficial = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOferta status = StatusOferta.ATIVA;

    @Column(name = "votos_ainda_tem")
    private Integer votosAindaTem = 0;

    @Column(name = "votos_acabou")
    private Integer votosAcabou = 0;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "loja_id", nullable = false)
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    public Oferta() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProdutoNome() { return produtoNome; }
    public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }

    // [NOVO] Getter e Setter para descricao
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public LocalDateTime getDataPostagem() { return dataPostagem; }
    public void setDataPostagem(LocalDateTime dataPostagem) { this.dataPostagem = dataPostagem; }

    public Boolean getIsOficial() { return isOficial; }
    public void setIsOficial(Boolean isOficial) { this.isOficial = isOficial; }

    public StatusOferta getStatus() { return status; }
    public void setStatus(StatusOferta status) { this.status = status; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Loja getLoja() { return loja; }
    public void setLoja(Loja loja) { this.loja = loja; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public Integer getVotosAindaTem() { return votosAindaTem; }
    public void setVotosAindaTem(Integer votosAindaTem) { this.votosAindaTem = votosAindaTem; }

    public Integer getVotosAcabou() { return votosAcabou; }
    public void setVotosAcabou(Integer votosAcabou) { this.votosAcabou = votosAcabou; }
}