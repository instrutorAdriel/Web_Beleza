package com.app.beleza.model;

public class AgendamentoDTO {

    private Long servicoId;

    private String nomeServico;
    private String descricao;
    private String imagem;
    private String unidade;
    private String bairro;
    private String duracao;



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

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
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

    public String getBairro() {
        return bairro;
    }

    public String getDuracao() {
        return duracao;
    }

    public Long getServicoId() {
        return servicoId;
    }

    public void setServicoId(Long servicoId) {
        this.servicoId = servicoId;
    }

}