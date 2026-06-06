package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import java.math.BigDecimal;

public record OfertaResponseDTO(
        Long id,
        String produtoNome,
        BigDecimal preco,
        String imagemUrl,
        String nomeLoja,
        String enderecoLoja,
        String categoria,
        Integer votosAcabou
) {
    
    public static OfertaResponseDTO fromEntity(Oferta oferta) {
        return new OfertaResponseDTO(
                oferta.getId(),
                oferta.getProdutoNome(),
                oferta.getPreco(),
                oferta.getImagemUrl(),
                oferta.getLoja().getNome(),
                oferta.getLoja().getEndereco(),
                oferta.getCategoria() != null ? oferta.getCategoria().getNome() : "Sem categoria",
                oferta.getVotosAcabou() != null ? oferta.getVotosAcabou() : 0
        );
    }
}