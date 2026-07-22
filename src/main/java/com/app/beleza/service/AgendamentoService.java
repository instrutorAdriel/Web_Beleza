package com.app.beleza.service;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Home;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.HomeRepository;
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

    public Agendamento criar(Usuario usuario, Long servicoId, String horario) {
        Optional<Home> servicoOpt = homeRepository.findById(servicoId);

        if (servicoOpt.isEmpty()) {
            return null;
        }

        Agendamento agendamento = new Agendamento(usuario, servicoOpt.get(), horario);
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
        return true;
    }
}
