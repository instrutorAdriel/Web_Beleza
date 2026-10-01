package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoAtendimentoRepository extends JpaRepository<Agendamento, Integer> {

    List<Agendamento>findByServicoId (Integer servicoId);

}
