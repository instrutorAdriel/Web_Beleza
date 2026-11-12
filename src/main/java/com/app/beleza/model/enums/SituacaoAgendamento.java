package com.app.beleza.model.enums;

public enum SituacaoAgendamento {
    CONFIRMADO,
    // Mantido apenas para ler registros antigos; novos agendamentos não usam este status.
    PENDENTE,
    REALIZADO,
    CANCELADO,
    NAO_COMPARECEU
}
