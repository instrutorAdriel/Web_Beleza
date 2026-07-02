package com.example.demo.service;

import com.example.demo.model.SessaoAtendimento;
import com.example.demo.model.SessaoAtendimentoDTO;
import com.example.demo.respository.SessaoAtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessaoAtendimentoService {

    @Autowired
    private SessaoAtendimentoRepository repository;

    public List<SessaoAtendimentoDTO> listarPorServico(Long servicoId) {
        List<SessaoAtendimento> sessoes = repository.findByServicoId(servicoId);

        return sessoes.stream().map(s -> new SessaoAtendimentoDTO(
                s.getId(),
                s.getDataAtendimento(),
                s.getHorarioInicial(),
                s.getVagasDisponiveis(),
                s.isAgendadoPeloUsuario()
        )).collect(Collectors.toList());
    }

    public void agendarSessao(Long id){
        SessaoAtendimento sessao = repository.findById(id).orElseThrow(()-> new RuntimeException("Sessão não encontrada!"));

        if(sessao.getVagasDisponiveis() <= 0) {
            throw new RuntimeException("Não há vagas disponíveis nesse horário");
        }

        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() -1);
        sessao.setAgendadoPeloUsuario(true);

        repository.save(sessao);

    }

}


