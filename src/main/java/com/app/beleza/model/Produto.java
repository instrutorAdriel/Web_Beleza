package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name="produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_produto")
    private Integer id;

    @Column(name="nome_produto", nullable = false, length = 60)
    private String nomeProduto;

    @Column(name="descricao", nullable = false, length = 255)
    private String descricao;

    @Column(name="imagemAnexo", nullable = false, length = 255)
    private String imagemAnexo;

    public Produto() {}

    public Produto(String nomeProduto, String descricao, String imagemAnexo) {
        this.nomeProduto = nomeProduto;
        this.descricao = descricao;
        this.imagemAnexo = imagemAnexo;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemAnexo() {
        return imagemAnexo;
    }

    public void setImagemAnexo(String imagemAnexo) {
        this.imagemAnexo = imagemAnexo;
    }
}
