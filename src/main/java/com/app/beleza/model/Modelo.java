package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="modelo")
public class Modelo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_modelo")
    private Integer idModelo;

    @Column(name="data_nascimento")
    private LocalDate dataNascimento;

    @Column(name="telefone", unique = true)
    private String telefone;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_usuario", nullable = false)
    private Usuario usuario;

    // Construtor limpo
    public Modelo() {}

    public Modelo(LocalDate dataNascimento, String telefone, Usuario usuario) {
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.usuario = usuario;
    }

    // Getters e Setters
    public Integer getIdModelo() {
        return idModelo;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
