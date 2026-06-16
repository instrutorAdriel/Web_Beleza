/**
 * Script de comportamento da tela de Login
 * Responsabilidade: Controlar elementos visuais da interface (ex: mostrar/esconder senha).
 * * NOTA: O redirecionamento de páginas após o login é controlado 100% pelo Back-end (Java/Spring Boot).
 */
document.addEventListener("DOMContentLoaded", function () {

    // Captura o botão do olhinho e o campo de texto da senha pelos IDs do HTML
    const btnToggle = document.getElementById("btnToggleLogin");
    const passwordInput = document.getElementById("password");

    // Verifica se os elementos realmente existem na tela antes de aplicar a lógica
    if (btnToggle && passwordInput) {

        btnToggle.addEventListener("click", function () {

            // Alterna o tipo do input entre 'password' (escondido) e 'text' (visível)
            if (passwordInput.type === "password") {
                passwordInput.type = "text";

                // Opcional: Adiciona uma classe CSS caso queira mudar a cor do botão ativo
                btnToggle.classList.add("password-visible");
            } else {
                passwordInput.type = "password";

                // Remove a classe CSS quando a senha volta a ficar oculta
                btnToggle.classList.remove("password-visible");
            }
        });
    }
});