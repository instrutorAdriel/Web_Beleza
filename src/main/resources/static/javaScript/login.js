/**
 * Script de comportamento da tela de Login
 * Responsabilidade: Controlar elementos visuais da interface (ex: mostrar/esconder senha).
 *
 * NOTA: O redirecionamento de páginas após o login é controlado 100% pelo Back-end (Java/Spring Boot).
 */
document.addEventListener("DOMContentLoaded", function () {

    // Captura o botão do olhinho e o campo de texto da senha pelos IDs do HTML
    const btnToggle = document.getElementById("btnToggleLogin");
    const passwordInput = document.getElementById("password");


    if (btnToggle && passwordInput) {

        btnToggle.addEventListener("click", function () {
            const icon = document.getElementById("eyeIconLogin");


            if (passwordInput.type === "password") {
                passwordInput.type = "text";
                btnToggle.classList.add("password-visible");
                if (icon) icon.classList.replace("fa-eye-slash", "fa-eye");
            } else {
                passwordInput.type = "password";
                btnToggle.classList.remove("password-visible");
                if (icon) icon.classList.replace("fa-eye", "fa-eye-slash");
            }
        });
    }
});