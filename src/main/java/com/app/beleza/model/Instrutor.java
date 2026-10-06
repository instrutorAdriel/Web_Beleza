package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name="instrutor")
public class Instrutor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_instrutor")
    private Integer idInstrutor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="id_usuario", nullable = false)
    private Usuario usuario;

    // Construtor vazio
    public Instrutor() {}

    public Instrutor(Usuario usuario) {
        this.usuario = usuario;
    }

    // Getters e Setters
    public Integer getIdInstrutor() {
        return idInstrutor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
