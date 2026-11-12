package com.app.beleza.model.dto;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.enums.SituacaoAgendamento;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoDTO(
        Integer idAgendamento,
        String nomeServico,
        String imagemAnexo,
        String nomeUnidade,
        String duracaoMinutos,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataAgendamento,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horaInicio,

        String statusTexto,
        String statusClasseCss
) {
    public AgendamentoDTO(Agendamento agendamento) {
        this(
                agendamento.getId(),

                // Navega até o Produto via Disponibilidade -> ProdutoUnidade -> Produto
                agendamento.getDisponibilidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade().getProduto() != null
                        ? agendamento.getDisponibilidade().getProdutoUnidade().getProduto().getNomeProduto()
                        : "Serviço Não Identificado",

                agendamento.getDisponibilidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade().getProduto() != null
                        ? agendamento.getDisponibilidade().getProdutoUnidade().getProduto().getImagemAnexo()
                        : "/img/servico-padrao.jpg",

                // Unidade
                agendamento.getDisponibilidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade() != null &&
                        agendamento.getDisponibilidade().getProdutoUnidade().getUnidade() != null
                        ? agendamento.getDisponibilidade().getProdutoUnidade().getUnidade().getNomeUnidade()
                        : "Unidade Não Informada",

                // Duração calculada (Hora Fim - Hora Início)
                calcularDuracao(agendamento),

                // Data e Hora de Início
                agendamento.getDisponibilidade() != null ? agendamento.getDisponibilidade().getDataDisponibilidade() : null,
                agendamento.getDisponibilidade() != null ? agendamento.getDisponibilidade().getHoraInicio() : null,

                // Registros legados pendentes são apresentados como confirmados.
                mapearStatusTexto(agendamento.getSituacao()),

                // Classe CSS dinamicamente mapeada
                mapearClasseCss(agendamento.getSituacao())
        );
    }

    private static String calcularDuracao(Agendamento agendamento) {
        if (agendamento.getDisponibilidade() != null &&
                agendamento.getDisponibilidade().getHoraInicio() != null &&
                agendamento.getDisponibilidade().getHoraFim() != null) {

            long minutos = Duration.between(
                    agendamento.getDisponibilidade().getHoraInicio(),
                    agendamento.getDisponibilidade().getHoraFim()
            ).toMinutes();

            return minutos + " min";
        }
        return "N/I";
    }

    private static String mapearClasseCss(SituacaoAgendamento situacao) {
        if (situacao == null) return "status-confirmado";

        return switch (situacao) {
            case CONFIRMADO, PENDENTE -> "status-confirmado";
            case REALIZADO -> "status-concluido";
            case CANCELADO, NAO_COMPARECEU -> "status-cancelado";
        };
    }

    private static String mapearStatusTexto(SituacaoAgendamento situacao) {
        return situacao == null || situacao == SituacaoAgendamento.PENDENTE
                ? SituacaoAgendamento.CONFIRMADO.name()
                : situacao.name();
    }
}