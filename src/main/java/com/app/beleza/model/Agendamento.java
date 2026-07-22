package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Home servico;

    @Column(nullable = false, length = 100)
    private String horario;

    @Column(nullable = false, length = 20)
    private String status; // CONFIRMADO ou CANCELADO

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Agendamento() {
    }

    public Agendamento(Usuario usuario, Home servico, String horario) {
        this.usuario = usuario;
        this.servico = servico;
        this.horario = horario;
        this.status = "CONFIRMADO";
        this.dataCriacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Home getServico() {
        return servico;
    }

    public void setServico(Home servico) {
        this.servico = servico;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
}
