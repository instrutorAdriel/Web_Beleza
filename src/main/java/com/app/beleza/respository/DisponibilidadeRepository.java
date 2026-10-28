package com.app.beleza.respository;

import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Integer> {
    Optional<Disponibilidade> findById(Integer id);

    @Query("SELECT d FROM Disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "WHERE d.id IN (" +
            "    SELECT MIN(sub.id) FROM Disponibilidade sub " +
            "    GROUP BY sub.produtoUnidade.produto.id, sub.produtoUnidade.unidade.id" +
            ")")
    Optional<List<Disponibilidade>> findServicosUnicosComRelacionamentos();
    Optional<List<Disponibilidade>> findByDataDisponibilidade(LocalDate dataDisponibilidade);

    // Procura disponibilidades com vagas ativas para uma data
    List<Disponibilidade> findByDataDisponibilidadeAndTotalVagasGreaterThan(LocalDate data, Integer vagas);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Disponibilidade d WHERE d.id = :id")
    Optional<Disponibilidade> findByIdComLock(@Param("id") Integer id);

    @Query("SELECT d FROM Disponibilidade d " +
            "JOIN FETCH d.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u " +
            "WHERE pu.id = :idProdutoUnidade " +
            "ORDER BY d.dataDisponibilidade ASC, d.horaInicio ASC")
    List<Disponibilidade> listarPorProdutoUnidade(@Param("idProdutoUnidade") Integer idProdutoUnidade);

}