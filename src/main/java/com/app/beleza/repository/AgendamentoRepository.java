package com.app.beleza.repository;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Instrutor;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.enums.SituacaoAgendamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    Optional<Agendamento> findAgendamentoById(Integer id);

    Optional<List<Agendamento>> findAgendamentoByModelo(Modelo modelo);

    Optional<List<Agendamento>> findAgendamentoByInstrutor(Instrutor instrutor);

    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto p " +
            "LEFT JOIN FETCH a.modelo m " +
            "LEFT JOIN FETCH m.usuario mu " +
            "WHERE mu.id = :usuarioId AND a.situacao = :situacao")
    List<Agendamento> findAgendamentosRealizadosPorUsuarioESituacao(@Param("usuarioId") Integer usuarioId,
                                                                    @Param("situacao") SituacaoAgendamento situacao);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Agendamento a " +
            "JOIN a.modelo m " +
            "JOIN m.usuario mu " +
            "WHERE mu.id = :usuarioId " +
            "AND a.disponibilidade.id = :disponibilidadeId " +
            "AND a.situacao <> :situacao")
    boolean existsByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
            @Param("usuarioId") Integer usuarioId,
            @Param("disponibilidadeId") Integer disponibilidadeId,
            @Param("situacao") SituacaoAgendamento situacao);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Agendamento a " +
            "JOIN a.disponibilidade d " +
            "JOIN a.modelo m " +
            "JOIN m.usuario mu " +
            "WHERE mu.id = :usuarioId " +
            "AND a.situacao <> :situacaoCancelada " +
            "AND d.dataDisponibilidade = :data " +
            "AND d.horaInicio < :horaFim " +
            "AND d.horaFim > :horaInicio")
    boolean existsConflitoDeHorario(@Param("usuarioId") Integer usuarioId,
                                    @Param("situacaoCancelada") SituacaoAgendamento situacaoCancelada,
                                    @Param("data") LocalDate data,
                                    @Param("horaInicio") LocalTime horaInicio,
                                    @Param("horaFim") LocalTime horaFim);

    @Query("SELECT a FROM Agendamento a " +
            "JOIN a.modelo m " +
            "JOIN m.usuario mu " +
            "WHERE mu.id = :usuarioId " +
            "AND a.disponibilidade.id = :disponibilidadeId " +
            "AND a.situacao <> :situacao " +
            "ORDER BY a.id ASC")
    List<Agendamento> findPorUsuarioEDisponibilidadeList(
            @Param("usuarioId") Integer usuarioId,
            @Param("disponibilidadeId") Integer disponibilidadeId,
            @Param("situacao") SituacaoAgendamento situacao);

    default Optional<Agendamento> findFirstByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
            Integer usuarioId, Integer disponibilidadeId, SituacaoAgendamento situacao) {
        return findPorUsuarioEDisponibilidadeList(usuarioId, disponibilidadeId, situacao)
                .stream()
                .findFirst();
    }

    @Query("SELECT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto " +
            "LEFT JOIN FETCH pu.unidade " +
            "LEFT JOIN FETCH a.instrutor i " +
            "LEFT JOIN FETCH a.modelo m " +
            "LEFT JOIN FETCH i.usuario iu " +
            "LEFT JOIN FETCH m.usuario mu " +
            "WHERE mu.id = :usuarioId " +
            "ORDER BY a.dataHoraAgendamento DESC")
    List<Agendamento> listarTodosPorUsuarioComJoins(@Param("usuarioId") Integer usuarioId);

    @Query(value = "SELECT a FROM Agendamento a " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "JOIN FETCH a.modelo m " +
            "JOIN FETCH m.usuario mu " +
            "WHERE m.idModelo = :idModelo OR mu.id = :idUsuario",
            countQuery = "SELECT COUNT(a) FROM Agendamento a " +
                    "JOIN a.modelo m " +
                    "JOIN m.usuario mu " +
                    "WHERE m.idModelo = :idModelo OR mu.id = :idUsuario")
    Page<Agendamento> findByModeloOrUsuarioPaginado(@Param("idModelo") Integer idModelo, @Param("idUsuario") Integer idUsuario, Pageable pageable);

    @Query(value = "SELECT a FROM Agendamento a " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "JOIN FETCH a.modelo m " +
            "JOIN FETCH m.usuario mu " +
            "WHERE mu.id = :idModelo " +
            "AND (:filtro IS NULL OR a.situacao = :filtro)",
            countQuery = "SELECT COUNT(a) FROM Agendamento a " +
                    "JOIN a.modelo m " +
                    "JOIN m.usuario mu " +
                    "WHERE mu.id = :idModelo " +
                    "AND (:filtro IS NULL OR a.situacao = :filtro)")
    Page<Agendamento> buscarPaginadoComFiltro(
            @Param("idModelo") Integer idModelo,
            @Param("filtro") SituacaoAgendamento filtro,
            Pageable pageable);

    Optional<Agendamento> findAgendamentoByIdAndModelo(@Param("agendamentoId") Integer id, @Param("modeloId") Modelo modelo);

    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto p " +
            "LEFT JOIN FETCH a.modelo m " +
            "LEFT JOIN FETCH m.usuario mu " +
            "WHERE mu.id = :usuarioId " +
            " AND a.situacao = :situacao " +
            " AND NOT EXISTS (" +
            " SELECT dep FROM Depoimento dep WHERE dep.agendamento.id = a.id" +
            ")")
    List<Agendamento> findAgendamentosRealizadosSemDepoimento(
            @Param("usuarioId") Integer usuarioId,
            @Param("situacao") SituacaoAgendamento situacao
    );

    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "JOIN FETCH a.modelo m " +
            "JOIN FETCH m.usuario mu " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade uni " +
            "WHERE a.situacao = :situacao " +
            "AND a.lembreteEnviado = false " +
            "AND a.dataHoraAgendamento BETWEEN :inicioJanela AND :fimJanela")
    Optional<List<Agendamento>> findAgendamentoParaLembrete(
            @Param("situacao") SituacaoAgendamento situacao,
            @Param("inicioJanela") LocalDateTime inicioJanela,
            @Param("fimJanela") LocalDateTime fimJanela
    );
}
