package com.app.beleza.model;

public class UsuarioDTO {

    private String nomeCompleto;
    private String dataNascimento;
    private String telefone;
    private String endereco;
    private String email;
    private String senha;            // Representa a Senha Atual no formulário de perfil
    private String novaSenha;        // Representa a Nova Senha no formulário
    private String confirmacaoSenha; // Representa a Confirmação da Nova Senha
    protected String token;

    // Getters e Setters
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(String dataNascimento) { this.dataNascimento = dataNascimento; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getNovaSenha() { return novaSenha; }
    public void setNovaSenha(String novaSenha) { this.novaSenha = novaSenha; }

    public String getConfirmacaoSenha() { return confirmacaoSenha; }
    public void setConfirmacaoSenha(String confirmacaoSenha) { this.confirmacaoSenha = confirmacaoSenha; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}