package com.example.demo.service;

import com.example.demo.model.PasswordReset;
import com.example.demo.model.Usuario;
import com.example.demo.respository.PasswordResetRepository;
import com.example.demo.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public String enviarEmailRecuperarSenha(String paraEmail, Usuario usuario){
        String tokenUUID = UUID.randomUUID().toString();

        PasswordReset novoToken = new PasswordReset(tokenUUID, usuario);

        if (!usuarioRepository.existsByEmail(usuario.getEmail())){
            return "Falha em encontrar o e-mail";
        }

        passwordResetRepository.save(novoToken);

        // Criar o código de enviar o email com o token
        emailService.enviarEmailDeRecuperacao(usuario.getEmail(), tokenUUID);

        IO.println("localhost:8080/usuario/alterar-senha/" + tokenUUID);

        return null;
    }

    public String verificarToken(String token_uuid){
        if (!passwordResetRepository.existsByToken(token_uuid)){
            IO.println("Token Inválido: " + token_uuid);
            return "Token inválido";
        }

        return null;
    }
}
