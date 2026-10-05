package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "unidade")
public class Unidade{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unidade")
    private Integer id;

    @Column(name = "nome_unidade", nullable = false, unique = true, length = 60)
    private String nome;

    @Column(name = "situacao", length = 1)
    private String situacao = "A";

    @Column(name = "endereco", length = 100)
    private String endereco;

    public Unidade() {}
    public Unidade(String nome, String situacao, String endereco) {
        this.nome = nome;
        this.situacao = situacao;
        this.endereco = endereco;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}