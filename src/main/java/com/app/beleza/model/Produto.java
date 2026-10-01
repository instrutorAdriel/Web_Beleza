package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "produto")
public class Produto {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_produto")
        private Long Id;

        @Column(name = "nome_produto", nullable = false, length = 100)
        private String nome;

        @Column(name = "descricao", nullable = false, length = 100)
        private String descricao;

        @Column(name = "imagem_anexo", nullable = false, length = 100)
        private String imagem;


    public Produto() {
    }
    public Produto(String nome, String descricao, String imagem) {
        this.nome = nome;
        this.descricao = descricao;
        this.imagem = imagem;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }
}

