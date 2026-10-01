package com.app.beleza.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "nome_usuario", nullable = false, length = 127)
    private String nomeCompleto;

    @Column(name = "senha", nullable = false, length = 255)
    private String senha;

    @Column(name = "situacao", nullable = false, length = 1)
    private String situacao = "A";

    public Usuario() {
    }

    public Usuario(String email, String nomeCompleto, String senha) {
        this.email = email;
        this.nomeCompleto = nomeCompleto;
        this.senha = senha;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getSituacao() { return situacao; }
    public void setSituacao(String situacao) { this.situacao = situacao; }
}