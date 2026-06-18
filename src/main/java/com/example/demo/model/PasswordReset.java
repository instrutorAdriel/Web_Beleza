package com.example.demo.model;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_passwordresettoken")
public class PasswordReset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_password_reset_token;

    @Column(nullable = false, length = 255)
    private String token;

    @Column(nullable = false)
    private LocalDateTime data_expiracao;

    @ManyToOne
    @JoinColumn(name = "id")
    private Usuario usuario;

    protected PasswordReset() {}

    public PasswordReset (String token, Usuario usuario){
        this.token = token;
        this.usuario = usuario;
        this.data_expiracao = LocalDateTime.now().plusMinutes(30);
    }

    // Getters e Setters

    public Long getId_password_reset_token() {
        return id_password_reset_token;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getData_expiracao() {
        return data_expiracao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
