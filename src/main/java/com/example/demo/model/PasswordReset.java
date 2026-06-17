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
    private Long id;

    @Column(nullable = false, length = 255)
    private String token_uuid;

    @Column(nullable = false)
    private LocalDateTime data_expiracao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    protected PasswordReset() {}

    public PasswordReset (String token){
        this.token_uuid = token;
        this.data_expiracao = LocalDateTime.now().plusMinutes(30);
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public String getToken_uuid() {
        return token_uuid;
    }

    public LocalDateTime getData_expiracao() {
        return data_expiracao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
