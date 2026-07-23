package com.app.beleza.model;

public class UsuarioDTO {

    private String nomeCompleto;
    private String dataNascimento;
    private String telefone;
    private String endereco;
    private String email;
    private String senha;
<<<<<<< HEAD
=======
    private String novaSenha;
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
    private String confirmacaoSenha;
    protected String token;

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

<<<<<<< HEAD
=======
    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public void setDataNascimento(String dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getDataNascimento() {
        return dataNascimento;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public String getSenha() {
        return senha;
    }

    public String getToken() {
        return token;
    }
}
