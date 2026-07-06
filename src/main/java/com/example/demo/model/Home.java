package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicos")
public class Home {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nomeServico;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, length = 500)
    private String imagem;

    @Column(nullable = false, length = 100)
    private String unidade;


    @Column(nullable = false, length = 50)
    private String duracao;

    public Home() {
    }

    public Home(String nomeServico, String descricao, String imagem,
                String unidade, String bairro, String duracao) {
        this.nomeServico = nomeServico;
        this.descricao = descricao;
        this.imagem = imagem;
        this.unidade = unidade;
        this.duracao = duracao;
    }

    public Long getId() {
        return id;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
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

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }


    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }
}