package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    long countByDisponibilidadeIdAndSituacaoAgendamentoNot(Long id, String situacaoAgendamento
    );
    Optional<Agendamento> findByDisponibilidadeIdAndUsuarioIdAndSituacaoAgendamentoNot(
            Long disponibilidadeId, Long usuarioId, String situacaoAgendamento);
}