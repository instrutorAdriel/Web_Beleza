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

    @OneToOne(optional = false)
    @JoinColumn(name = "id_unidade", nullable = false)
    private Unidade unidade;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    public ProdutoUnidade() {
    }

    public ProdutoUnidade(String produtoUtilizado, Unidade unidade, Produto produto) {
        this.produtoUtilizado = produtoUtilizado;
        this.unidade = unidade;
        this.produto = produto;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        this.Id = id;
    }

    public String getProdutoUtilizado() {
        return produtoUtilizado;
    }

    public void setProdutoUtilizado(String produtoUtilizado) {
        this.produtoUtilizado = produtoUtilizado;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

}
