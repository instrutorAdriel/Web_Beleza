package com.app.beleza.respository;

import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DisponibilidadeRepository extends JpaRepository<Disponibilidade, Integer> {
    Optional<Disponibilidade> findById(Integer id);

    Optional<List<Disponibilidade>> findByDataDisponibilidade(LocalDate dataDisponibilidade);

    // Procura disponibilidades com vagas ativas para uma data
    List<Disponibilidade> findByDataDisponibilidadeAndTotalVagasGreaterThan(LocalDate data, Integer vagas);


    Optional<List<Disponibilidade>> findServicosUnicosComRelacionamentos();
}