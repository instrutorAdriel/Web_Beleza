package com.app.beleza.service;

import com.app.beleza.model.Usuario;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdminAuthorizationServiceTest {

    @Test
    void autorizaEmailInstitucionalSemDiferenciarMaiusculas() {
        AdminAuthorizationService service = new AdminAuthorizationService();
        Usuario usuario = new Usuario();
        usuario.setEmail(" instrutor@DF.SENAC.BR ");

        assertTrue(service.isAdministrador(usuario));
    }

    @Test
    void naoAutorizaEmailComumDominioParecidoOuFormatoInvalido() {
        AdminAuthorizationService service = new AdminAuthorizationService();
        Usuario usuarioComum = new Usuario();
        usuarioComum.setEmail("cliente@exemplo.com");
        Usuario dominioParecido = new Usuario();
        dominioParecido.setEmail("instrutor@df.senac.br.exemplo.com");
        Usuario dominioNoNome = new Usuario();
        dominioNoNome.setEmail("instrutor@df.senac.br.evil.com");
        Usuario sufixoSemSeparador = new Usuario();
        sufixoSemSeparador.setEmail("instrutor@subdf.senac.br");

        assertFalse(service.isAdministrador(usuarioComum));
        assertFalse(service.isAdministrador(dominioParecido));
        assertFalse(service.isAdministrador(dominioNoNome));
        assertFalse(service.isAdministrador(sufixoSemSeparador));
        assertFalse(service.isAdministrador(null));
        assertFalse(service.isAdministrador(new Usuario()));
    }
}
