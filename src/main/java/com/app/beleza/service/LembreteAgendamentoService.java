package com.app.beleza.service;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.enums.SituacaoAgendamento;
import com.app.beleza.respository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LembreteAgendamentoService {
    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private EmailService emailService;

    // Executa a cada 15 minutos (900.000 milisegundos)
    @Scheduled(fixedRate = 900000)
    @Transactional
    public void processarLembrete24Horas() {
        LocalDateTime agora = LocalDateTime.now();

        LocalDateTime inicioJanela = agora.plusHours(23).plusMinutes(45);
        LocalDateTime fimJanela = agora.plusHours(24).plusMinutes(15);

        Optional<List<Agendamento>> optional = agendamentoRepository.findAgendamentoParaLembrete(
                SituacaoAgendamento.CONFIRMADO,
                inicioJanela,
                fimJanela
        );

        List<Agendamento> agendamentosPendentes = optional.orElse(null);

        assert agendamentosPendentes != null;
        if (!agendamentosPendentes.isEmpty()) {
            for (Agendamento agendamento : agendamentosPendentes) {
                try {
                    emailService.enviarLembreteAgendamento(agendamento);

                    agendamento.setLembreteEnviado(true);
                    agendamentoRepository.save(agendamento);
                } catch (Exception e) {
                    System.err.println("Erro ao enviar lembrete do agendamento #" + agendamento.getId() + ": " + e.getMessage());
                }
            }
        }
    }
}
