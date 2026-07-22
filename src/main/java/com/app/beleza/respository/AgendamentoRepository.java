package com.app.beleza.respository;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}
