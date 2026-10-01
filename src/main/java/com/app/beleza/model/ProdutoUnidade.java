package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name="produto_unidade")
public class ProdutoUnidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto_unidade")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_unidade")
    private Unidade unidade;

    public ProdutoUnidade(Produto produto, Unidade unidade) {
        this.produto = produto;
        this.unidade = unidade;
    }

    public ProdutoUnidade() {}

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        this.unidade = unidade;
    }
}
