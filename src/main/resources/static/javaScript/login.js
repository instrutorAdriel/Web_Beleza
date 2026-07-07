/**
 * Script de comportamento da tela de Login
 * Responsabilidade: Controlar elementos visuais da interface (ex: mostrar/esconder senha).
 *
 * NOTA: O redirecionamento de páginas após o login é controlado 100% pelo Back-end (Java/Spring Boot).
 */
/**
 * Script de comportamento da tela de Login
 * Responsabilidade: Controlar elementos visuais da interface (ex: mostrar/esconder senha).
 *
 * NOTA: O redirecionamento de páginas após o login é controlado 100% pelo Back-end (Java/Spring Boot).
 */
document.addEventListener("DOMContentLoaded", function () {

    const btnToggle = document.getElementById("btnToggleLogin");
    const passwordInput = document.getElementById("password");

    if (btnToggle && passwordInput) {

        btnToggle.addEventListener("click", function () {
            const icon = document.getElementById("eyeIcon1"); // id correto do HTML

            if (passwordInput.type === "password") {
                passwordInput.type = "text";
                if (icon) icon.classList.replace("fa-eye-slash", "fa-eye");
            } else {
                passwordInput.type = "password";
                if (icon) icon.classList.replace("fa-eye", "fa-eye-slash");
            }
        });
    }
});