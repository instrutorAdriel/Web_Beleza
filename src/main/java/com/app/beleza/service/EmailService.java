package com.app.beleza.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String REMETENTE = "naoresponde@suportesenac.br";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void enviarEmailDeRecuperacao(String destinatario, String token) {
        String link = "http://localhost:8080/alterar-senha/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Link de Recuperação de Senha");
        message.setText("Para recuperar sua senha, clique no link abaixo:\n" +
                link +
                "\n\nSe você recebeu esse e-mail sem o seu consentimento, por favor ignore e entre em contato com suporte.");
        message.setFrom(REMETENTE);

        mailSender.send(message);
    }

    public void enviarConfirmacao(String destinatario, String nomeCliente, LocalTime hora_inicial, String data_atendimento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Agendamento Confirmado - Senac");
        message.setText("Olá " + nomeCliente + "!\n\n" +
                "Seu agendamento foi confirmado com sucesso:\n" +
                "Data/Hora: " + hora_inicial.toString() + " " + data_atendimento + "\n\n" +
                "Você receberá um lembrete 24h antes.\n\n" +
                "Equipe - SENAC DF");
        message.setFrom(REMETENTE);

        mailSender.send(message);
    }

    public void enviarLembrete(String destinatario, String nomeCliente, LocalTime hora_inicial, String data_atendimento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Lembrete de Agendamento - Senac");
        message.setText("Olá " + nomeCliente + "!\n\n" +
                "Este é um lembrete do seu agendamento:\n" +
                "Data/Hora: " + hora_inicial.toString() + " " + data_atendimento + "\n\n" +
                "Contamos com sua presença!\n\n" +
                "Equipe  - SENAC DF");
        message.setFrom(REMETENTE);

        mailSender.send(message);
    }
}