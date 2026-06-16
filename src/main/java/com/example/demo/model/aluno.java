package com.example.demo.model;

public class aluno {
    private String nome;
    private String curso;

    public aluno(String nome, String curso) {
        this.nome = nome;
        this.curso = curso;
    }

    public String getNome() {
        return nome;
    }

    public String getCurso() {
        return curso;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }
}
