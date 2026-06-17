package com.example.demo.service;

import com.example.demo.model.PasswordReset;
import com.example.demo.respository.PasswordResetRepository;
import com.example.demo.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    public String enviarEmailRecuperarSenha(String paraEmail){
        String tokenUUID = UUID.randomUUID().toString();

        PasswordReset novoToken = new PasswordReset(tokenUUID);

        passwordResetRepository.save(novoToken);

        // Criar o código de enviar o email com o token


        return null;
    }
}
