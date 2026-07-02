package com.example.demo.respository;

import com.example.demo.model.SessaoAtendimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessaoAtendimentoRepository extends JpaRepository<SessaoAtendimento, Long> {

    List<SessaoAtendimento>findByServicoId (long servicoId);

}
