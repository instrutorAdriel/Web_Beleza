package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "depoimento")
public class Depoimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_depoimento")
    private Integer id;

    @Column(name="depoimento", nullable = false, length = 255)
    private String depoimento;

    @Column(name="avaliacao", nullable = false)
    private Integer avaliacao;

    @Column(name="imagem_anexo_1", nullable = false, length = 255)
    private String imagemAnexo1;

    @Column(name="imagem_anexo_2", nullable = false, length = 255)
    private String imagemAnexo2;

    @OneToOne
    @JoinColumn(name="id_modelo", nullable = false)
    private Modelo modelo;

    @OneToOne
    @JoinColumn(name="id_usuario", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name="id_agendamento", nullable = false)
    private Agendamento agendamento;

    public Depoimento() {
    }

    public Depoimento(String depoimento, Integer avaliacao, String imagemAnexo1, String imagemAnexo2, Modelo modelo, Usuario usuario, Agendamento agendamento) {
        this.depoimento = depoimento;
        this.avaliacao = avaliacao;
        this.imagemAnexo1 = imagemAnexo1;
        this.imagemAnexo2 = imagemAnexo2;
        this.modelo = modelo;
        this.usuario = usuario;
        this.agendamento = agendamento;
    }

    // Getters e Setters

    public Integer getId() {
        return id;
    }

    public String getDepoimento() {
        return depoimento;
    }

    public void setDepoimento(String depoimento) {
        this.depoimento = depoimento;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getImagemAnexo1() {
        return imagemAnexo1;
    }

    public void setImagemAnexo1(String imagemAnexo1) {
        this.imagemAnexo1 = imagemAnexo1;
    }

    public String getImagemAnexo2() {
        return imagemAnexo2;
    }

    public void setImagemAnexo2(String imagemAnexo2) {
        this.imagemAnexo2 = imagemAnexo2;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Agendamento getAgendamento() {
        return agendamento;
    }

    public void setAgendamento(Agendamento agendamento) {
        this.agendamento = agendamento;
    }
}