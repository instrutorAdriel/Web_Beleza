package com.example.demo.model;

public class Financeiro {
    // Ajustado para bater com o Controller (nome, valor, data)
    private String nome;
    private String valor;
    private String data;

    // Construtor padrão necessário para o Jackson (Spring) trabalhar com JSON
    public Financeiro() {
    }

    // Construtor completo que você usa no return do seu Controller
    public Financeiro(String nome, String valor, String data) {
        this.nome = nome;
        this.valor = valor;
        this.data = data;
    }

    // Getters e Setters Atualizados
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}