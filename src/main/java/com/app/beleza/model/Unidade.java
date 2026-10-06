package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name="unidade")
public class Unidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_unidade")
    private Integer id;

    @Column(name="nome_unidade", nullable = false, length = 60)
    private String nomeUnidade;

    @Column(name="situacao", nullable = false, length = 1)
    private char situacao;

    @Column(name = "endereco", nullable = false, length = 100)
    private String endereco;

    public Unidade() {
    }

    public Unidade(String nomeUnidade, char situacao, String endereco) {
        this.nomeUnidade = nomeUnidade;
        this.situacao = situacao;
        this.endereco = endereco;
    }

    // Getters e Setters
    public void setNomeUnidade(String nomeUnidade) {
        this.nomeUnidade = nomeUnidade;
    }

    public void setSituacao(char situacao) {
        this.situacao = situacao;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getId() {
        return id;
    }

    public String getNomeUnidade() {
        return nomeUnidade;
    }

    public char getSituacao() {
        return situacao;
    }

    public String getEndereco() {
        return endereco;
    }
}
