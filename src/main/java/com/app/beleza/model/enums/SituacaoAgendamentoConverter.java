package com.app.beleza.model.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.text.Normalizer;
import java.util.Locale;

@Converter
public class SituacaoAgendamentoConverter implements AttributeConverter<SituacaoAgendamento, String> {

    @Override
    public String convertToDatabaseColumn(SituacaoAgendamento situacao) {
        return situacao == null ? null : situacao.name();
    }

    @Override
    public SituacaoAgendamento convertToEntityAttribute(String situacao) {
        if (situacao == null || situacao.isBlank()) {
            return null;
        }

        String normalizada = Normalizer.normalize(situacao.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "_")
                .replaceAll("^_|_$", "");

        try {
            return SituacaoAgendamento.valueOf(normalizada);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Situação de agendamento desconhecida no banco: " + situacao, e);
        }
    }
}
