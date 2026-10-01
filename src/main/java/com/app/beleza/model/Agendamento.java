package com.app.beleza.model;

import com.app.beleza.model.enums.SituacaoAgendamento;
import jakarta.persistence.*;
import org.springframework.boot.Banner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agendamento")
public class Agendamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_agendamento")
    private Long id;

    private LocalDateTime data_hora_agendamento;

    private SituacaoAgendamento situacao;

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

    public Agendamento(LocalDateTime data_hora_agendamento, SituacaoAgendamento situacao, String observacao, Disponibilidade disponibilidade, Modelo modelo, Instrutor instrutor) {
        this.data_hora_agendamento = data_hora_agendamento;
        this.situacao = situacao;
        this.observacao = observacao;
        this.disponibilidade = disponibilidade;
        this.modelo = modelo;
        this.instrutor = instrutor;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public LocalDateTime getData_hora_agendamento() {
        return data_hora_agendamento;
    }

    public void setData_hora_agendamento(LocalDateTime data_hora_agendamento) {
        this.data_hora_agendamento = data_hora_agendamento;
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
