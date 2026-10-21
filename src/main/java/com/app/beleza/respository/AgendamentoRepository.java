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
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {

    Optional<Agendamento> findAgendamentoById(Integer id);

    Optional<List<Agendamento>> findAgendamentoByModelo(Modelo modelo);

    Optional<List<Agendamento>> findAgendamentoByInstrutor(Instrutor instrutor);

    List<Agendamento> findByUsuarioIdAndSituacao(Integer usuarioId, SituacaoAgendamento situacao);

    // Consulta filtrando diretamente pelo usuario.id da tabela Agendamento
    @Query("SELECT DISTINCT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto p " +
            "WHERE a.usuario.id = :usuarioId AND a.situacao = :situacao")
    List<Agendamento> findAgendamentosRealizadosPorUsuario(@Param("usuarioId") Integer usuarioId,
                                                           @Param("situacao") SituacaoAgendamento situacao);


    boolean existsByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
            Integer usuarioId, Integer disponibilidadeId, SituacaoAgendamento situacao);

    Optional<Agendamento> findFirstByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
            Integer usuarioId, Integer disponibilidadeId, SituacaoAgendamento situacao);

    @Query("SELECT a FROM Agendamento a " +
            "LEFT JOIN FETCH a.disponibilidade d " +
            "LEFT JOIN FETCH d.produtoUnidade pu " +
            "LEFT JOIN FETCH pu.produto " +
            "LEFT JOIN FETCH pu.unidade " +
            "LEFT JOIN FETCH a.instrutor i " +
            "LEFT JOIN FETCH i.usuario " +
            "WHERE a.usuario.id = :usuarioId " +
            "ORDER BY a.dataHoraAgendamento DESC")
    List<Agendamento> listarTodosPorUsuarioComJoins(@Param("usuarioId") Integer usuarioId);

    @Query(value = "SELECT a FROM Agendamento a " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "WHERE a.modelo.idModelo = :idModelo OR a.modelo.usuario.id = :idUsuario",
            countQuery = "SELECT COUNT(a) FROM Agendamento a WHERE a.modelo.idModelo = :idModelo OR a.modelo.usuario.id = :idUsuario")
    Page<Agendamento> findByModeloOrUsuarioPaginado(@Param("idModelo") Integer idModelo, @Param("idUsuario") Integer idUsuario, Pageable pageable);

    // Busca paginada trazendo todos ou filtrando por situação
    @Query(value = "SELECT a FROM Agendamento a " +
            "JOIN FETCH a.disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "WHERE a.modelo.idModelo = :idModelo " +
            "AND (:filtro IS NULL OR a.situacao = :filtro)",
            countQuery = "SELECT COUNT(a) FROM Agendamento a " +
                    "WHERE a.modelo.idModelo = :idModelo " +
                    "AND (:filtro IS NULL OR a.situacao = :filtro)")
    Page<Agendamento> buscarPaginadoComFiltro(
            @Param("idModelo") Integer idModelo,
            @Param("filtro") SituacaoAgendamento filtro,
            Pageable pageable);

}