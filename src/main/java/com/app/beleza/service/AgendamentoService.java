package com.app.beleza.service;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Home;
import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.HomeRepository;
import com.app.beleza.respository.SessaoAtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private SessaoAtendimentoRepository sessaoAtendimentoRepository;

    public Agendamento criar(Usuario usuario, Long servicoId, String horario) {
        return criar(usuario, servicoId, horario, null);
    }

    public Agendamento criar(Usuario usuario, Long servicoId, String horario, Long sessaoId) {
        Optional<Home> servicoOpt = homeRepository.findById(servicoId);

        if (servicoOpt.isEmpty()) {
            return null;
        }

        Agendamento agendamento = new Agendamento(usuario, servicoOpt.get(), horario, sessaoId);
        return agendamentoRepository.save(agendamento);
    }


    public List<Agendamento> listarPorUsuario(Usuario usuario) {
        return agendamentoRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
    }


    public boolean cancelar(Long agendamentoId, Usuario usuario) {
        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(agendamentoId);

        if (agendamentoOpt.isEmpty()) {
            return false;
        }

        Agendamento agendamento = agendamentoOpt.get();

        if (!agendamento.getUsuario().getId().equals(usuario.getId())) {
            return false;
        }

        if ("CANCELADO".equals(agendamento.getStatus())) {
            return true;
        }

        agendamento.setStatus("CANCELADO");
        agendamentoRepository.save(agendamento);

        // Se este agendamento veio de uma SessaoAtendimento (fluxo real de agendamento
        // pela Home), devolve a vaga e remove o usuário de lá também, para não deixar
        // a vaga "presa" nem impedir o usuário de agendar de novo.
        if (agendamento.getSessaoId() != null) {
            sessaoAtendimentoRepository.findById(agendamento.getSessaoId()).ifPresent(sessao -> {
                boolean estavaNaLista = sessao.getUsuarios().removeIf(u -> u.getId().equals(usuario.getId()));
                if (estavaNaLista) {
                    sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() + 1);
                    sessaoAtendimentoRepository.save(sessao);
                }
            });
        }

        return true;
    }

    // Usado pela SessaoAtendimentoService quando o usuário desfaz o agendamento
    // pelo modal da Home, para manter o histórico do /perfil sincronizado.
    public void cancelarPorSessao(Usuario usuario, Long sessaoId) {
        agendamentoRepository
                .findByUsuarioAndSessaoIdAndStatus(usuario, sessaoId, "CONFIRMADO")
                .ifPresent(agendamento -> {
                    agendamento.setStatus("CANCELADO");
                    agendamentoRepository.save(agendamento);
                });
    }
}
