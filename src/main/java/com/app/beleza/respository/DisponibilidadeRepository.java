package com.app.beleza.respository;

import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Integer> {
    Optional<Disponibilidade> findById(Integer id);

    Optional<List<Disponibilidade>> findByDataDisponibilidade(LocalDate dataDisponibilidade);

    // Procura disponibilidades com vagas ativas para uma data
    List<Disponibilidade> findByDataDisponibilidadeAndTotalVagasGreaterThan(LocalDate data, Integer vagas);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Disponibilidade d WHERE d.id = :id")
    Optional<Disponibilidade> findByIdComLock(@Param("id") Integer id);
}