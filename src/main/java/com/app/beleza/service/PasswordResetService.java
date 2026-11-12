package com.app.beleza.service;

import com.app.beleza.model.PasswordReset;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.PasswordResetRepository;
import com.app.beleza.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.mail.MailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PasswordResetService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);

    // Valor especial devolvido quando já existe um código válido.
    // A controller usa isso para mostrar o aviso ao usuário.
    public static final String EMAIL_JA_ENVIADO = "EMAIL_JA_ENVIADO";

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    // Retorna: null = enviado com sucesso | EMAIL_JA_ENVIADO = já existe código válido | outro texto = erro
    public String enviarEmailRecuperarSenha(String paraEmail, Usuario usuario) {

        if (!usuarioRepository.existsByEmail(paraEmail)) {
            return "Falha em encontrar o e-mail";
        }

        // Procura tokens desse usuário que ainda não expiraram
        List<PasswordReset> validos =
                passwordResetRepository.buscarTokensValidos(usuario, LocalDateTime.now());

        // Já existe código válido: não cria outro e não reenvia, só avisa
        if (!validos.isEmpty()) {
            return EMAIL_JA_ENVIADO;
        }

        // Não existe código válido: cria e salva um novo
        PasswordReset reset = passwordResetRepository.save(
                new PasswordReset(UUID.randomUUID().toString(), usuario));

        try {
            emailService.enviarEmailDeRecuperacao(paraEmail, reset.getToken());
        } catch (MailException ex) {
            logger.error("Falha ao enviar e-mail de recuperação de senha para {}", paraEmail, ex);
            passwordResetRepository.delete(reset); // se o e-mail falhou, apaga o token
            return "Falha ao enviar o e-mail de recuperação";
        }

        return null;
    }

    // Confere se o token existe e não expirou
    public String verificarToken(String token_uuid) {
        Optional<PasswordReset> resetOpt = passwordResetRepository.findByToken(token_uuid);

        if (resetOpt.isEmpty()) {
            return "Token inválido";
        }

        PasswordReset psr = resetOpt.get();

        if (LocalDateTime.now().isAfter(psr.getData_expiracao())) {
            return "Token expirado";
        }

        return null;
    }

    // Devolve o usuário dono do token
    public Usuario retornarUsuario(String token_uuid) {
        return passwordResetRepository.findByToken(token_uuid).get().getUsuario();
    }

    // Apaga o token depois que a senha foi trocada (o link não pode ser usado duas vezes)
    public void invalidarToken(String token_uuid) {
        passwordResetRepository.findByToken(token_uuid)
                .ifPresent(passwordResetRepository::delete);
    }
}