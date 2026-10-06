package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Instrutor;
import com.app.beleza.model.Modelo;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Integer> {
    Optional<Agendamento> findAgendamentoById(Integer id);

    Optional<List<Agendamento>> findAgendamentoByModelo(Modelo modelo);

    Optional<List<Agendamento>> findAgendamentoByInstrutor(Instrutor instrutor);
}
