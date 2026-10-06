package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Instrutor;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.enums.SituacaoAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    Optional<Agendamento> findAgendamentoById(Integer id);

    Optional<List<Agendamento>> findAgendamentoByModelo(Modelo modelo);

    Optional<List<Agendamento>> findAgendamentoByInstrutor(Instrutor instrutor);

    // Consulta filtrando diretamente pelo usuario.id da tabela Agendamento
    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto p " +
            "WHERE a.usuario.id = :usuarioId AND a.situacao = :situacao")
    List<Agendamento> findAgendamentosRealizadosPorUsuario(@Param("usuarioId") Integer usuarioId,
                                                           @Param("situacao") SituacaoAgendamento situacao);
}