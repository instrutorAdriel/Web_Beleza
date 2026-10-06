package com.app.beleza.model.dto;

public class DepoimentoDTO {

    // Colunas diretas do banco de dados (Tabela Depoimento)
    private Long idDepoimento;
    private String depoimento;
    private Integer avaliacao;
    private String imagemAnexo1;
    private String imagemAnexo2;
    private Integer idModelo;
    private Integer idUsuario;
    private Integer idAgendamento;

    // Campos auxiliares requeridos pela HomeService e Cards do HTML
    private String nome;      // Mapeado a partir de depoimento.getUsuario().getNomeUsuario()
    private String servico;   // Mapeado a partir do Produto
    private String unidade;   // Mapeado a partir da Unidade
    private String texto;     // Alias/Cópia para o campo depoimento

    // Construtor Padrão
    public DepoimentoDTO() {}

    // Getters e Setters do Banco de Dados
    public Long getIdDepoimento() { return idDepoimento; }
    public void setIdDepoimento(Long idDepoimento) { this.idDepoimento = idDepoimento; }

    public String getDepoimento() { return depoimento; }
    public void setDepoimento(String depoimento) {
        this.depoimento = depoimento;
        this.texto = depoimento; // Mantém o texto sincronizado automaticamente
    }

    public Integer getAvaliacao() { return avaliacao; }
    public void setAvaliacao(Integer avaliacao) { this.avaliacao = avaliacao; }

    public String getImagemAnexo1() { return imagemAnexo1; }
    public void setImagemAnexo1(String imagemAnexo1) { this.imagemAnexo1 = imagemAnexo1; }

    public String getImagemAnexo2() { return imagemAnexo2; }
    public void setImagemAnexo2(String imagemAnexo2) { this.imagemAnexo2 = imagemAnexo2; }

    public Integer getIdModelo() { return idModelo; }
    public void setIdModelo(Integer idModelo) { this.idModelo = idModelo; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Integer getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Integer idAgendamento) { this.idAgendamento = idAgendamento; }

    // Getters e Setters utilizados pela HomeService
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getTexto() { return texto; }
    public void setTexto(String texto) {
        this.texto = texto;
        this.depoimento = texto; // Sincroniza também caso o setTexto seja chamado
    }

    // Aliases para manter compatibilidade com setImagem1 e setImagem2 chamados na HomeService
    public String getImagem1() { return imagemAnexo1; }
    public void setImagem1(String imagem1) { this.imagemAnexo1 = imagem1; }

    public String getImagem2() { return imagemAnexo2; }
    public void setImagem2(String imagem2) { this.imagemAnexo2 = imagem2; }

    // Método utilitário para gerar a letra do Avatar nos cards da Home
    public String getInicialAvatar() {
        return nome != null && !nome.isEmpty() ? nome.substring(0, 1).toUpperCase() : "?";
    }
}