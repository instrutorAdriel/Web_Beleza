package com.app.beleza.respository;

import com.app.beleza.model.Depoimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Integer> {
    Optional<Depoimento> findByAgendamentoId(Integer agendamentoId);

    List<Depoimento> findByUsuarioId(Integer usuarioId);
}