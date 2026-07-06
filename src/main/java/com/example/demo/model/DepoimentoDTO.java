package com.example.demo.model;

public class DepoimentoDTO {

    private String nome;
    private String servico;
    private String unidade;
    private String texto;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }
    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getInicialAvatar() {
        return nome != null && !nome.isEmpty() ? nome.substring(0, 1).toUpperCase() : "?";
    }

    private String imagem1;
    private String imagem2;

    public String getImagem1() { return imagem1; }
    public void setImagem1(String imagem1) { this.imagem1 = imagem1; }
    public String getImagem2() { return imagem2; }
    public void setImagem2(String imagem2) { this.imagem2 = imagem2; }
}