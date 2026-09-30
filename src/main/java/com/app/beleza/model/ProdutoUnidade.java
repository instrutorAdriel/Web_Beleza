package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table (name = "produto_unidade")
public class ProdutoUnidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto_unidade")
    private Long Id;

    @Column(name = "produto_utilizado", nullable = false, length = 100)
    private String produtoUtilizado;

    @Column(name = "", nullable = false, length = 100)
    private String descricao;

    @Column(name = "imagem_anexo", nullable = false, length = 100)
    private String imagem;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;


}
