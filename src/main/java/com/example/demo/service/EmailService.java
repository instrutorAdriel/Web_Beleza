package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailDeRecuperacao(String destinatario, String token){
        String link = "http://localhost:8080/alterar-senha/" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinatario);
        message.setSubject("Link de Recuperação de Senha");
        message.setText("Para recuperar sua senha, clique no link abaixo:\n" +
                link +
                "\n\nSe você recebeu esse esse e-mail sem o seu consentimento, por favor ignore e entre em contato com suporte.");
        message.setFrom("naoresponde@suportesenac.br");

        mailSender.send(message);
    }
}
