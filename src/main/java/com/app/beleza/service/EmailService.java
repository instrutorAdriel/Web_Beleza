package com.app.beleza.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Value("${app.mail.from:agendamentosenac@gmail.com}")
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
}
