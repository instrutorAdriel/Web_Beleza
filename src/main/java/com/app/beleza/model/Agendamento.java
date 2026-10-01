package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agendamento")
    private Integer id;

    @Column(name = "data_hora_agendamento")
    private LocalDateTime dataHora;

    @Column(name = "situacao")
    private String SituacaoAgendamento;

    @Column(name = "observacao", length = 255)
    private String observacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_disponibilidade", nullable = false)
    private Disponibilidade disponibilidade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_modelo", nullable = false)
    private Modelo modelo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public Agendamento(LocalDateTime dataHora, String situacaoAgendamento, String observacao, Disponibilidade disponibilidade, Modelo modelo, Usuario usuario) {
        this.dataHora = dataHora;
        this.SituacaoAgendamento = situacaoAgendamento;
        this.observacao = observacao;
        this.disponibilidade = disponibilidade;
        this.modelo = modelo;
        this.usuario = usuario;
    }

    public Agendamento() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getSituacaoAgendamento() {
        return SituacaoAgendamento;
    }

    public void setSituacaoAgendamento(String situacaoAgendamento) {
        SituacaoAgendamento = situacaoAgendamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Disponibilidade getDisponibilidade() {
        return disponibilidade;
    }

    public void setDisponibilidade(Disponibilidade disponibilidade) {
        this.disponibilidade = disponibilidade;
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
}
