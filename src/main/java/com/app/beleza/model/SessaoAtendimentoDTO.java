package com.app.beleza.model;

public class SessaoAtendimentoDTO {

    private Long id;
    private String dataAtendimento;
    private String horarioInicial;
    private Integer vagasDisponiveis;
    private boolean agendadoPeloUsuario;

    public SessaoAtendimentoDTO(Long id, String dataAtendimento, String horarioInicial, Integer vagasDisponiveis, boolean agendadoPeloUsuario) {
        this.id = id;
        this.dataAtendimento = dataAtendimento;
        this.horarioInicial = horarioInicial;
        this.vagasDisponiveis = vagasDisponiveis;
        this.agendadoPeloUsuario = agendadoPeloUsuario;
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

    public void setHorarioInicial(String horaInicial) {
        this.horarioInicial = horaInicial;
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
