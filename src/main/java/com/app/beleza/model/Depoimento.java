package com.app.beleza.model;

import com.app.beleza.model.enums.StatusModeracaoDepoimento;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "depoimento")
public class Depoimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_depoimento")
    private Integer id;

    @Column(name="depoimento", nullable = false, length = 255)
    private String depoimento;

    @Column(name="imagem_anexo_1", nullable = false, length = 255)
    private String imagemAnexo1;

    @Column(name="imagem_anexo_2", nullable = false, length = 255)
    private String imagemAnexo2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_moderacao", nullable = false, length = 20)
    private StatusModeracaoDepoimento statusModeracao = StatusModeracaoDepoimento.PENDENTE;

    @CreationTimestamp
    @Column(name = "data_envio", nullable = false, updatable = false)
    private LocalDateTime dataEnvio;

    @ManyToOne
    @JoinColumn(name="id_modelo", nullable = false)
    private Modelo modelo;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false)
    private Usuario usuario;

    @OneToOne
    @JoinColumn(name="id_agendamento", nullable = false)
    private Agendamento agendamento;

    public Depoimento() {
    }

    public Depoimento(String depoimento, String imagemAnexo1, String imagemAnexo2, Modelo modelo, Usuario usuario, Agendamento agendamento) {
        this.depoimento = depoimento;
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

    public StatusModeracaoDepoimento getStatusModeracao() {
        return statusModeracao;
    }

    public void setStatusModeracao(StatusModeracaoDepoimento statusModeracao) {
        this.statusModeracao = statusModeracao;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
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