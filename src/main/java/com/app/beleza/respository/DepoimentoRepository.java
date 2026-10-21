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
    @Query("SELECT d FROM Depoimento d " +
            "JOIN FETCH d.usuario u " +
            "JOIN FETCH d.agendamento a " +
            "JOIN FETCH a.disponibilidade disp " +
            "JOIN FETCH disp.produtoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade unid")
    Optional<List<Depoimento>> findAllComRelacionamento();
}