package com.app.beleza.model.dto;

public class ServicoDisponibilidadeDTO {

    private Integer servicoId;

    private String nomeServico;
    private String descricao;
    private String imagem;
    private String unidade;
    private String hora_inicio;
    private String hora_fim;

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getImagem() {
        return imagem;
    }

    public String getUnidade() {
        return unidade;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_fim() {
        return hora_fim;
    }

    public void setHora_fim(String hora_fim) {
        this.hora_fim = hora_fim;
    }

    public void setServicoId(Integer servicoId) {
        this.servicoId = servicoId;
    }

}