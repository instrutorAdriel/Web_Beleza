package com.app.beleza.service;

import com.app.beleza.model.Agendamento;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${app.mail.from}")
    private String remetente;

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailDeRecuperacao(String destinatario, String token){
        String link = baseUrl.replaceAll("/+$", "") + "/alterar-senha/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Link de Recuperação de Senha");
        message.setText("Para recuperar sua senha, clique no link abaixo:\n" +
                link +
                "\n\nSe você recebeu esse esse e-mail sem o seu consentimento, por favor ignore e entre em contato com suporte.");
        message.setFrom(remetente);

        mailSender.send(message);
    }

    public void enviarLembreteAgendamento(Agendamento agendamento) {
        String emailDestino = agendamento.getUsuario().getEmail();
        String nomeUsuario = agendamento.getUsuario().getNomeUsuario();
        String nomeServico = agendamento.getDisponibilidade().getProdutoUnidade().getProduto().getNomeProduto();
        String nomeUnidade = agendamento.getDisponibilidade().getProdutoUnidade().getUnidade().getNomeUnidade();
        String dataFormatada = agendamento.getDataHoraAgendamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

        String assunto = "Lembrete de Agendamento - SENAC Beleza";
        String corpoHtml = """
                <div style="font-family: Arial, sans-serif; padding: 20px; color: #333;">
                    <h2 style="color: #004a8d;">Olá, %s!</h2>
                    <p>Este é um lembrete de que o seu procedimento está agendado para amanhã.</p>
        
                    <div style="background-color: #f8fafc; border-left: 4px solid #f7941d; padding: 15px; margin: 20px 0;">
                        <p style="margin: 5px 0;"><strong>Serviço:</strong> %s</p>
                        <p style="margin: 5px 0;"><strong>Unidade:</strong> %s</p>
                        <p style="margin: 5px 0;"><strong>Data e Horário:</strong> %s</p>
                    </div>
        
                    <p>Contamos com a sua presença! Caso precise cancelar, acesse a aba <em>Meus Agendamentos</em> no seu perfil.</p>
                </div>
                """.formatted(nomeUsuario, nomeServico, nomeUnidade, dataFormatada);

        enviarEmailHtml(emailDestino, assunto, corpoHtml);
    }

    public void enviarEmailHtml(String destinatario, String assunto, String corpoHtml) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(corpoHtml, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Falha ao enviar e-mail HTML para " + destinatario, e);
        }
    }
}
