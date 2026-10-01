/**
 * Script de comportamento da tela de Login
 *
 * Responsabilidade:
 * Controlar a visualização da senha através do botão do olho.
 *
 * REGRA DO SISTEMA:
 *
 * 👁️ Olho destampado = senha ESCONDIDA
 *
 * 🙈 Olho tampado = senha VISÍVEL
 *
 * O login e o redirecionamento são controlados pelo Back-end.
 */

document.addEventListener("DOMContentLoaded", function () {

    // Campo da senha
    const passwordInput = document.getElementById("password");

    // Botão do olho
    const btnToggle = document.getElementById("btnToggleLogin");

    // Ícone do olho
    const icon = document.getElementById("eyeIcon1");


    // Verifica se os elementos existem
    if (!passwordInput || !btnToggle || !icon) {
        return;
    }


    // Quando clicar no olho
    btnToggle.addEventListener("click", function () {


        /*
         * Se a senha estiver escondida...
         */
        if (passwordInput.type === "password") {

            // MOSTRA a senha
            passwordInput.type = "text";


            // Troca para OLHO TAMPADO
            icon.classList.remove("fa-eye");
            icon.classList.add("fa-eye-slash");


            // Atualiza a descrição do botão
            btnToggle.setAttribute(
                "aria-label",
                "Ocultar senha"
            );


        } else {


            /*
             * Se a senha estiver aparecendo...
             */

            // ESCONDE a senha
            passwordInput.type = "password";


            // Troca para OLHO DESTAMPADO
            icon.classList.remove("fa-eye-slash");
            icon.classList.add("fa-eye");


            // Atualiza a descrição do botão
            btnToggle.setAttribute(
                "aria-label",
                "Mostrar senha"
            );
        }

    });

});