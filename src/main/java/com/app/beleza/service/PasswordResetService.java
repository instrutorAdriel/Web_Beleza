package com.app.beleza.service;

import com.app.beleza.model.PasswordReset;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.PasswordResetRepository;
import com.app.beleza.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
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

        IO.println("localhost:8080/alterar-senha/" + tokenUUID);

        return null;
    }

    public String verificarToken(String token_uuid){
        Optional<PasswordReset> resetOpt = passwordResetRepository.findByToken(token_uuid);

        if (resetOpt.isEmpty()){
            return "Token inválido";
        }

        PasswordReset psr = resetOpt.get();

        if (LocalDateTime.now().isAfter(psr.getData_expiracao())){
            return "Token expirado";
        }

        return null;
    }

    public Usuario retornarUsuario(String token_uuid){
        return passwordResetRepository.findByToken(token_uuid).get().getUsuario();
    }
}
