package com.app.beleza.respository;

import com.app.beleza.model.Depoimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Integer> {
    // Procura um depoimento associado a um agendamento específico
    Optional<Depoimento> findByAgendamentoId(Integer agendamentoId);

    // Procura todos os depoimentos de um utilizador
    List<Depoimento> findByUsuarioId(Integer usuarioId);
}