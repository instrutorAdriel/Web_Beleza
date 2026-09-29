package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class
Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_usuario;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String nome_usuario;

    @Column(nullable = false, length = 100)
    private String senha;

    @Column(nullable = false, length = 100)
    private String situacao;


    public Usuario() {
    }
    public Usuario(String email, String nome_usuario, String senha, String situacao) {
        this.email = email;
        this.nome_usuario = nome_usuario;
        this.senha = senha;
        this.situacao = situacao;
    }

    public Long getId_usuario() {
        return id_usuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNome_usuario() {
        return nome_usuario;
    }

    public void setNome_usuario(String nome_usuario) {
        this.nome_usuario = nome_usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}