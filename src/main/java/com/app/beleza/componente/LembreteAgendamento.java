package com.app.beleza.componente;
import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.SessaoAtendimentoRepository;
import com.app.beleza.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class LembreteAgendamento {
    @Autowired
    private SessaoAtendimentoRepository repository;

    @Autowired
    private EmailService emailService;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "0 * * * * *") // roda de minuto a minuto
    @Transactional
    public void verificarSessoesProximas() {
        List<SessaoAtendimento> sessoes = repository.findByLembreteEnviadoFalse();
        LocalDateTime agora = LocalDateTime.now();

        for (SessaoAtendimento sessao : sessoes) {
            LocalDateTime dataHoraSessao;

            try {
                dataHoraSessao = LocalDateTime.of(
                        LocalDate.parse(sessao.getDataAtendimento(), FORMATO_DATA),
                        sessao.getHorarioInicial()
                );
            } catch (Exception e) {
                System.out.println("Erro ao interpretar data/hora da sessão " + sessao.getId() + ": " + e.getMessage());
                continue; // pula essa sessão e vai pra próxima
            }

            long horasAteSessao = Duration.between(agora, dataHoraSessao).toHours();

            // dispara quando estiver entre 23h e 24h de antecedência
            if (horasAteSessao >= 23 && horasAteSessao <= 24) {
                for (Usuario usuario : sessao.getUsuarios()) {
                    IO.println("verificarSessoesProximas: " + usuario.getEmail());
                    try {
                        emailService.enviarLembrete(
                                usuario.getEmail(),
                                usuario.getNomeCompleto(),
                                sessao.getHorarioInicial(),
                                sessao.getDataAtendimento()
                        );
                    } catch (Exception e) {
                        System.out.println("Erro ao enviar lembrete para " + usuario.getEmail() + ": " + e.getMessage());
                    }
                }

                sessao.setLembreteEnviado(true);
                repository.save(sessao);
            }
        }
    }
}
