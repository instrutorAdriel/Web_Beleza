package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_usuario")
    private Integer id;

    @Column(name="nome_usuario", nullable = false, length = 127)
    private String nomeUsuario;

    @Column(name="email", nullable = false, length = 100)
    private String email;

    @Column(name="senha", nullable = false, length = 255)
    private String senha;

    @Column(name="situacao", nullable = false)
    private char situacao;

    // Construtor limpo
    public Usuario(){}

    public Usuario(String nomeUsuario, String email, String senha, char situacao) {
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.senha = senha;
        this.situacao = situacao;
    }

    // Getters e Setters
    public Integer getId() {
        return id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public char getSituacao() {
        return situacao;
    }

    public void setSituacao(char situacao) {
        this.situacao = situacao;
    }
}