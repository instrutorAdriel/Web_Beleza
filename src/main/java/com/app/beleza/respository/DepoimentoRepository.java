package com.app.beleza.respository;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Disponibilidade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Integer> {
    void deleteByUsuarioId(Integer usuarioId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Depoimento d " +
            "WHERE d.usuario.id <> :usuarioId AND (" +
            "d.agendamento.usuario.id = :usuarioId OR " +
            "(:modeloId IS NOT NULL AND d.agendamento.modelo.idModelo = :modeloId) OR " +
            "(:modeloId IS NOT NULL AND d.modelo.idModelo = :modeloId))")
    boolean existeDepoimentoDeOutraPessoaVinculadoAConta(@Param("usuarioId") Integer usuarioId,
                                                          @Param("modeloId") Integer modeloId);

    @Query(value = "SELECT d FROM Depoimento d " +
            "JOIN FETCH d.usuario u " +
            "JOIN FETCH d.agendamento a " +
            "JOIN FETCH a.disponibilidade disp " +
            "JOIN FETCH disp.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade unid " +
            "WHERE (:unidade IS NULL OR unid.nomeUnidade = :unidade) " +
            "AND (:servico IS NULL OR p.nomeProduto = :servico) " +
            "AND (unid.situacao='A') " +
            "ORDER BY a.dataHoraAgendamento DESC",
            countQuery = "SELECT count(d) FROM Depoimento d " +
                    "JOIN d.agendamento a " +
                    "JOIN a.disponibilidade disp " +
                    "JOIN disp.produtoUnidade pu " +
                    "JOIN pu.produto p " +
                    "JOIN pu.unidade unid " +
                    "WHERE (:unidade IS NULL OR unid.nomeUnidade = :unidade) " +
                    "AND (unid.situacao='A')" +
                    "AND (:servico IS NULL OR p.nomeProduto = :servico)")

    Page<Depoimento> buscarDepoimentosFiltrados(
            @Param("unidade") String unidade,
            @Param("servico") String servico,
            Pageable pageable
    );
}
