package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessoesAtendimento")
public class SessaoAtendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "servico_id", nullable = false)
    private Long servicoId;

    @Column(name = "data_atendimento", nullable = false, length = 100)
    private String dataAtendimento;

    @Column(name = "lembrete_enviado", nullable = false)
    private boolean lembreteEnviado = false;

    public boolean isLembreteEnviado() {
        return lembreteEnviado;
    }

    public void setLembreteEnviado(boolean lembreteEnviado) {
        this.lembreteEnviado = lembreteEnviado;
    }

    @Column(name = "horario_inicial", nullable = false, length = 100)
    private LocalTime horarioInicial;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "agendado_pelo_usuario", nullable = false)
    private boolean agendadoPeloUsuario;



    @ManyToMany
    @JoinTable(
            name = "sessao_usuarios",
            joinColumns = @JoinColumn(name = "sessao_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<Usuario> usuarios = new ArrayList<>();

    public List<Usuario> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Usuario> usuarios) { this.usuarios = usuarios; }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getDataAtendimento() {
        return dataAtendimento;
    }
    public void setDataAtendimento(String dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public LocalTime getHorarioInicial() {
        return horarioInicial;
    }
    public void setHorarioInicial(LocalTime horarioInicial) {
        this.horarioInicial = horarioInicial;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }
    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public boolean isAgendadoPeloUsuario() {
        return agendadoPeloUsuario;
    }
    public void setAgendadoPeloUsuario(boolean agendadoPeloUsuario) {
        this.agendadoPeloUsuario = agendadoPeloUsuario;
    }
}
