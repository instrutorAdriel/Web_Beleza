package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "depoimentos")
public class Depoimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String servico;

    @Column(nullable = false, length = 100)
    private String unidade;

    @Column(nullable = false, length = 500)
    private String texto;

    public Depoimento() {}

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }


    @Column(length = 500)
    private String imagem1;

    @Column(length = 500)
    private String imagem2;

    public String getImagem1() { return imagem1; }
    public void setImagem1(String imagem1) { this.imagem1 = imagem1; }
    public String getImagem2() { return imagem2; }
    public void setImagem2(String imagem2) { this.imagem2 = imagem2; }
}