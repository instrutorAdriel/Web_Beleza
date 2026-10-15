package com.app.beleza.respository;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Integer> {
    // Procura um depoimento associado a um agendamento específico
    Optional<Depoimento> findByAgendamentoId(Integer agendamentoId);

    // Procura todos os depoimentos de um utilizador
    List<Depoimento> findByUsuarioId(Integer usuarioId);
    // Retorna apenas 1 registro por Serviço/Produto e Unidade (evitando múltiplos cards para horários diferentes)
    @Query("SELECT d FROM Disponibilidade d " +
            "WHERE d.id IN (" +
            "    SELECT MIN(sub.id) FROM Disponibilidade sub " +
            "    GROUP BY sub.produtoUnidade.produto.id, sub.produtoUnidade.unidade.id" +
            ")")
    List<Disponibilidade> findServicosUnicos();
}