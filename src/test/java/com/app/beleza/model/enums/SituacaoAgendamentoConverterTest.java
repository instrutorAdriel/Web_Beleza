package com.app.beleza.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SituacaoAgendamentoConverterTest {

    private final SituacaoAgendamentoConverter converter = new SituacaoAgendamentoConverter();

    @Test
    void normalizaFormatosDeStatusGravadosPorOutrasAplicacoes() {
        assertEquals(SituacaoAgendamento.CONFIRMADO, converter.convertToEntityAttribute("Confirmado"));
        assertEquals(SituacaoAgendamento.CONFIRMADO, converter.convertToEntityAttribute(" CONFIRMADO "));
        assertEquals(SituacaoAgendamento.NAO_COMPARECEU, converter.convertToEntityAttribute("Não compareceu"));
    }

    @Test
    void gravaStatusNoFormatoPadraoDaAplicacaoWeb() {
        assertEquals("CONFIRMADO", converter.convertToDatabaseColumn(SituacaoAgendamento.CONFIRMADO));
    }

    @Test
    void rejeitaStatusDesconhecidoComMensagemClara() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> converter.convertToEntityAttribute("Status inválido")
        );

        assertEquals("Situação de agendamento desconhecida no banco: Status inválido", exception.getMessage());
    }
}
