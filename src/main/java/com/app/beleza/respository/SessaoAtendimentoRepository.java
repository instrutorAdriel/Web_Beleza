package com.app.beleza.respository;

import com.app.beleza.model.SessaoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoAtendimentoRepository extends JpaRepository<SessaoAtendimento, Long> {

    List<SessaoAtendimento>findByServicoId (long servicoId);
    List<SessaoAtendimento> findByLembreteEnviadoFalse();

}
