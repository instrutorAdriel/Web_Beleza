package com.app.beleza.model;

import com.app.beleza.model.enums.StatusModeracaoDepoimento;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DepoimentoModeracaoTest {

    @Test
    void depoimentoNovoAguardaModeracao() {
        assertEquals(StatusModeracaoDepoimento.PENDENTE, new Depoimento().getStatusModeracao());
    }
}
