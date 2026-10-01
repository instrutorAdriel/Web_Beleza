package com.app.beleza.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "disponibilidade")
public class Disponibilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_disponibilidade")
    private Long id;

    @Column(name="total_vagas", nullable = false)
    private int totalVagas;

    @Column(name="hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name="hora_fim", nullable = false)
    private LocalTime horaFim;

    @Column(name="data_disponibilidade", nullable = false)
    private LocalDate dataDisponibilidade;

    @ManyToOne
    @JoinColumn(name = "id_produto_unidade", nullable = false)
    private ProdutoUnidade produtoUnidade;

    public Disponibilidade() {}

    public Disponibilidade(int totalVagas, LocalTime horaInicio, LocalTime horaFim, LocalDate dataDisponibilidade, ProdutoUnidade produtoUnidade) {
        this.totalVagas = totalVagas;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
        this.dataDisponibilidade = dataDisponibilidade;
        this.produtoUnidade = produtoUnidade;
    }

    // Getter e Setters

    public Long getId() {
        return id;
    }

    public int getTotalVagas() {
        return totalVagas;
    }

    public void setTotalVagas(int totalVagas) {
        this.totalVagas = totalVagas;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public LocalDate getDataDisponibilidade() {
        return dataDisponibilidade;
    }

    public void setDataDisponibilidade(LocalDate dataDisponibilidade) {
        this.dataDisponibilidade = dataDisponibilidade;
    }

    public ProdutoUnidade getProdutoUnidade() {
        return produtoUnidade;
    }

    public void setProdutoUnidade(ProdutoUnidade produtoUnidade) {
        this.produtoUnidade = produtoUnidade;
    }
}