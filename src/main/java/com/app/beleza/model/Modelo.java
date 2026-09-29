package com.app.beleza.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "modelo")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_modelo")
    private Integer id;

    @Column(name = "telefone", unique = true, length = 16)
    private String telefone;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @OneToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    public Modelo() {
    }

    public Modelo(String telefone, LocalDate dataNascimento, Usuario usuario) {
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.usuario = usuario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}