package com.app.beleza.respository;

import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Long> {
    Optional<Disponibilidade> findById(Long id);

    Optional<List<Disponibilidade>> findByDataDisponibilidade(LocalDate dataDisponibilidade);
}
