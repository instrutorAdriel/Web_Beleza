package com.app.beleza.service;

import com.app.beleza.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AdminAuthorizationService {

    private static final String DOMINIO_ADMIN = "@df.senac.br";

    public boolean isAdministrador(Usuario usuario) {
        if (usuario == null || usuario.getEmail() == null) {
            return false;
        }

        String email = usuario.getEmail().trim().toLowerCase(Locale.ROOT);
        int separador = email.indexOf('@');
        return separador > 0
                && email.endsWith(DOMINIO_ADMIN)
                && separador == email.length() - DOMINIO_ADMIN.length();
    }
}
