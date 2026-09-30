package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    long countByDisponibilidadeIdAndSituacaoNot(Integer disponibilidadeId, String situacao);

    Optional<Agendamento> findByDisponibilidadeIdAndUsuarioIdAndSituacaoNot(
            Integer disponibilidadeId, Integer usuarioId, String situacao);
}