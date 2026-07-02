package com.example.demo.model;

import jakarta.persistence.*;

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

    @Column(name = "horario_inicial", nullable = false, length = 100)
    private String horarioInicial;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "agendado_pelo_usuario", nullable = false)
    private boolean agendadoPeloUsuario;

    // ... mantenha os getters e setters iguais abaixo


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

    public String getHorarioInicial() {
        return horarioInicial;
    }
    public void setHorarioInicial(String horarioInicial) {
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
