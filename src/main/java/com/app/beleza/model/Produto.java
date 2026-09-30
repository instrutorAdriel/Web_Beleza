package com.app.beleza.model;

import jakarta.persistence.*;

public class Produto {
    @Entity
    @Table(name = "produto")
    public class Produto{
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

    }
}