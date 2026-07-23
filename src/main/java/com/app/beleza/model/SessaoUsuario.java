package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(name = "sessao_usuarios")
public class SessaoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sessao_id")
    private SessaoAtendimento sessao;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "lembrete_enviado", nullable = false)
    private boolean lembreteEnviado = false;

    public boolean isLembreteEnviado() {
        return lembreteEnviado;
    }

    public void setLembreteEnviado(boolean lembreteEnviado) {
        this.lembreteEnviado = lembreteEnviado;
    }

    public SessaoAtendimento getSessao_id() {
        return sessao;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
