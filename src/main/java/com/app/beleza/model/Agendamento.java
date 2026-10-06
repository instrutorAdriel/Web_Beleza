package com.app.beleza.model;

import com.app.beleza.model.enums.SituacaoAgendamento;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamento")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_agendamento")
    private Integer id;

    @Column(name="data_hora_agendamento", nullable = false)
    private LocalDateTime dataHoraAgendamento;

    @Enumerated(EnumType.STRING)
    @Column(name="situacao", nullable = false)
    private SituacaoAgendamento situacao;

    @Column(name="observacao", nullable = false, length = 255)
    private String observacao;

    @ManyToOne
    @JoinColumn(name="id_disponibilidade")
    private Disponibilidade disponibilidade;

    @ManyToOne
    @JoinColumn(name="id_modelo")
    private Modelo modelo;

    @ManyToOne
    @JoinColumn(name="id_instrutor")
    private Instrutor instrutor;

    public Agendamento() {}

    public Agendamento(LocalDateTime dataHoraAgendamento, SituacaoAgendamento situacao, String observacao, Disponibilidade disponibilidade, Modelo modelo, Instrutor instrutor) {
        this.dataHoraAgendamento = dataHoraAgendamento;
        this.situacao = situacao;
        this.observacao = observacao;
        this.disponibilidade = disponibilidade;
        this.modelo = modelo;
        this.instrutor = instrutor;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public LocalDateTime getDataHoraAgendamento() {
        return dataHoraAgendamento;
    }

    public void setDataHoraAgendamento(LocalDateTime dataHoraAgendamento) {
        this.dataHoraAgendamento = dataHoraAgendamento;
    }

    public SituacaoAgendamento getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAgendamento situacao) {
        this.situacao = situacao;
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

    public Instrutor getInstrutor() {
        return instrutor;
    }

    public void setInstrutor(Instrutor instrutor) {
        this.instrutor = instrutor;
    }
}
