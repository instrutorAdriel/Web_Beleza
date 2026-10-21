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
import org.springframework.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PasswordResetService {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    public String enviarEmailRecuperarSenha(String paraEmail, Usuario usuario){
        String tokenUUID = UUID.randomUUID().toString();

        PasswordReset novoToken = new PasswordReset(tokenUUID, usuario);

        if (!usuarioRepository.existsByEmail(paraEmail)){
            return "Falha em encontrar o e-mail";
        }

        passwordResetRepository.save(novoToken);

        try {
            emailService.enviarEmailDeRecuperacao(paraEmail, tokenUUID);
        } catch (MailException ex) {
            logger.error("Falha ao enviar e-mail de recuperação de senha para {}", paraEmail, ex);
            System.err.println("[EMAIL-RECUPERACAO] " + ex.getClass().getName() + ": " + ex.getMessage());
            ex.printStackTrace(System.err);
            passwordResetRepository.delete(novoToken);
            return "Falha ao enviar o e-mail de recuperação";
        }

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
