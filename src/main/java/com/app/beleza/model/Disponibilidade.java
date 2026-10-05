package com.app.beleza.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "disponibilidade")
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_disponibilidade")
    private Long id;

    @Column(name = "total_vagas", nullable = false, length = 100)
    private String vagasDisponiveis;

    @Column(name = "data_disponibilidade")
    private LocalDate DataAtendimento;

    @Column(name = "hora_inicio")
    private LocalTime HorarioInicial;

    @Column(name = "hora_fim")
    private LocalTime horaFim;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_produto_unidade", nullable = false)
    private ProdutoUnidade produtoUnidade;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public void setVagasDisponiveis(String vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }

    public LocalDate getDataAtendimento() {
        return DataAtendimento;
    }

    public void setDataAtendimento(LocalDate dataAtendimento) {
        DataAtendimento = dataAtendimento;
    }

    public LocalTime getHorarioInicial() {
        return HorarioInicial;
    }

    public void setHorarioInicial(LocalTime horarioInicial) {
        HorarioInicial = horarioInicial;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public ProdutoUnidade getProdutoUnidade() {
        return produtoUnidade;
    }

    public void setProdutoUnidade(ProdutoUnidade produtoUnidade) {
        this.produtoUnidade = produtoUnidade;
    }
}
