package br.edu.ifpi.picos.backend_vp.dto;

import br.edu.ifpi.picos.backend_vp.model.Oferta;
import br.edu.ifpi.picos.backend_vp.model.enums.StatusOferta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// [CORREÇÃO] DTO enriquecido: o frontend recebe tudo que precisa em uma única chamada.
// Antes: retornava só IDs de loja/categoria — o frontend teria que fazer chamadas extras
// para montar cada card do feed (problema N+1 de requisições).
// Agora: loja e categoria já vêm como objetos com id + nome.
public record OfertaResponseDTO(
        Long id,
        String produtoNome,
        String descricao,          // [NOVO] campo de descrição
        BigDecimal preco,
        String imagemUrl,
        StatusOferta status,       // [NOVO] útil para o frontend mostrar badges (Ativa, Expirada...)
        LocalDateTime dataPostagem,// [NOVO] para ordenação e exibição ("postado há 2h")
        Integer votosAcabou,
        Integer votosAindaTem,     // [NOVO] o outro lado da votação
        LojaResumoDTO loja,        // [CORREÇÃO] objeto aninhado com id + nome + endereço
        CategoriaResumoDTO categoria // [CORREÇÃO] objeto aninhado com id + nome
) {

    // DTOs internos leves para o contexto da oferta
    // Separados como records internos para não criar arquivos extras à toa
    public record LojaResumoDTO(Long id, String nome, String endereco, String whatsapp) {}
    public record CategoriaResumoDTO(Long id, String nome) {}

    public static OfertaResponseDTO fromEntity(Oferta oferta) {

        LojaResumoDTO lojaDTO = new LojaResumoDTO(
                oferta.getLoja().getId(),
                oferta.getLoja().getNome(),
                oferta.getLoja().getEndereco(),
                oferta.getLoja().getWhatsapp()
        );

        CategoriaResumoDTO categoriaDTO = oferta.getCategoria() != null
                ? new CategoriaResumoDTO(
                        oferta.getCategoria().getId(),
                        oferta.getCategoria().getNome())
                : new CategoriaResumoDTO(null, "Sem categoria");

        return new OfertaResponseDTO(
                oferta.getId(),
                oferta.getProdutoNome(),
                oferta.getDescricao(),
                oferta.getPreco(),
                oferta.getImagemUrl(),
                oferta.getStatus(),
                oferta.getDataPostagem(),
                oferta.getVotosAcabou() != null ? oferta.getVotosAcabou() : 0,
                oferta.getVotosAindaTem() != null ? oferta.getVotosAindaTem() : 0,
                lojaDTO,
                categoriaDTO
        );
    }
}