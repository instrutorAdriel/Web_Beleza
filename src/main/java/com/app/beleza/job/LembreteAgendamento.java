package com.app.beleza.job;

import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.SessaoUsuario;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.SessaoUsuarioRepository;
import com.app.beleza.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class LembreteAgendamento {
    @Autowired
    private SessaoUsuarioRepository sessaoUsuarioRepository;

    @Autowired
    private EmailService emailService;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "0 * * * * *") // roda de minuto a minuto
    @Transactional
    public void verificarSessoesProximas() {
        Optional<List<SessaoUsuario>> sessoes = sessaoUsuarioRepository.findByLembreteEnviadoFalse();
        LocalDateTime agora = LocalDateTime.now();

        for (SessaoUsuario sessao : sessoes.get()) {
            LocalDateTime dataHoraSessao;
            SessaoAtendimento sessaoAtendimento = sessao.getSessao_id();

            try {
                dataHoraSessao = LocalDateTime.of(
                        LocalDate.parse(sessaoAtendimento.getDataAtendimento(), FORMATO_DATA),
                        sessaoAtendimento.getHorarioInicial()
                );
            } catch (Exception e) {
                System.out.println("Erro ao interpretar data/hora da sessão " + sessao.getSessao_id() + ": " + e.getMessage());
                continue; // pula essa sessão e vai pra próxima
            }

            long horasAteSessao = Duration.between(agora, dataHoraSessao).toHours();

            // dispara quando estiver entre 23h e 24h de antecedência
            Usuario usuario = sessao.getUsuario();
            if (horasAteSessao >= 23 && horasAteSessao <= 24) {
                IO.println("verificarSessoesProximas: " + usuario.getEmail());
                try {
                    emailService.enviarLembrete(
                            usuario.getEmail(),
                            usuario.getNomeCompleto(),
                            sessaoAtendimento.getHorarioInicial(),
                            sessaoAtendimento.getDataAtendimento()
                    );
                } catch (Exception e) {
                    System.out.println("Erro ao enviar lembrete para " + usuario.getEmail() + ": " + e.getMessage());
                }

                sessao.setLembreteEnviado(true);
                sessaoUsuarioRepository.save(sessao);
            }
        }
    }
}
