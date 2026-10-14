package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Instrutor;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.enums.SituacaoAgendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    List<Agendamento> findAgendamentosRealizadosPorUsuarioESituacao(@Param("usuarioId") Integer usuarioId,
                                                                    @Param("situacao") SituacaoAgendamento situacao);

    @Query(value = "SELECT a FROM Agendamento a " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "WHERE a.modelo.idModelo = :idModelo OR a.modelo.usuario.id = :idUsuario",
            countQuery = "SELECT COUNT(a) FROM Agendamento a WHERE a.modelo.idModelo = :idModelo OR a.modelo.usuario.id = :idUsuario")
    Page<Agendamento> findByModeloOrUsuarioPaginado(@Param("idModelo") Integer idModelo, @Param("idUsuario") Integer idUsuario, Pageable pageable);
}