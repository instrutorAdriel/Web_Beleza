package com.app.beleza.service;

import com.app.beleza.model.PasswordReset;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.PasswordResetRepository;
import com.app.beleza.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;          // NOVO: usado pela lista de tokens válidos
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

    // ALTERADO: antes sempre criava um token novo. Agora reaproveita o válido.
    public String enviarEmailRecuperarSenha(String paraEmail, Usuario usuario) {

        // (igual a antes) se o e-mail não existe, para aqui.
        // Agora essa checagem vem ANTES de criar qualquer coisa.
        if (!usuarioRepository.existsByEmail(paraEmail)) {
            return "Falha em encontrar o e-mail";
        }

        // NOVO: procura tokens desse usuário que ainda não expiraram
        List<PasswordReset> validos =
                passwordResetRepository.buscarTokensValidos(usuario, LocalDateTime.now());

        // NOVO: true = já existe um código válido, não precisa criar outro
        boolean reaproveitado = !validos.isEmpty();

        // NOVO: se já existe, usa o primeiro (o mais recente).
        // Se não existe, cria um novo e salva no banco (como era antes).
        PasswordReset reset = reaproveitado
                ? validos.get(0)
                : passwordResetRepository.save(new PasswordReset(UUID.randomUUID().toString(), usuario));

        try {
            // ALTERADO: envia o token do "reset" (novo OU reaproveitado),
            // em vez do UUID que era gerado sempre
            emailService.enviarEmailDeRecuperacao(paraEmail, reset.getToken());
        } catch (MailException ex) {
            // ALTERADO: removi o System.err.println e o printStackTrace,
            // o logger.error já registra o erro completo
            logger.error("Falha ao enviar e-mail de recuperação de senha para {}", paraEmail, ex);

            // NOVO: só apaga o token se ele foi criado agora.
            // Um token reaproveitado continua no banco, pois ainda é válido.
            if (!reaproveitado) {
                passwordResetRepository.delete(reset);
            }
            return "Falha ao enviar o e-mail de recuperação";
        }

        return null; // null = sucesso (igual a antes)
    }

    // (sem mudanças) confere se o token existe e não expirou
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

    // (sem mudanças) devolve o usuário dono do token
    public Usuario retornarUsuario(String token_uuid) {
        return passwordResetRepository.findByToken(token_uuid).get().getUsuario();
    }

    // NOVO: apaga o token depois que a senha foi trocada,
    // para o mesmo link não ser usado duas vezes
    public void invalidarToken(String token_uuid) {
        passwordResetRepository.findByToken(token_uuid)
                .ifPresent(passwordResetRepository::delete);
    }
}