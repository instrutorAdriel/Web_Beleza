document.addEventListener("DOMContentLoaded", function () {
    // Seleção dos elementos do formulário
    const form = document.getElementById("form-alterar-senha");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const errorWarning = document.getElementById("password-error");

    // Seleção dos botões de alternar senha (olho)
    const btnToggle1 = document.getElementById("btnToggle1");
    const btnToggle2 = document.getElementById("btnToggle2");

    // Ícones SVG para controle visual do olho (Visível / Oculto)
    const svgEyeOpen = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path><circle cx="12" cy="12" r="3"></circle></svg>`;
    const svgEyeClosed = `<svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"></path><line x1="1" y1="1" x2="23" y2="23"></line></svg>`;

    /**
     * Função que valida se os dois campos de senha são idênticos
     */
    function verificarSenhas() {
        // Só valida se o usuário já tiver digitado algo no campo de confirmação
        if (confirmaSenhaInput.value === "") {
            errorWarning.style.display = "none";
            senhaInput.classList.remove("input-error");
            confirmaSenhaInput.classList.remove("input-error");
            return true;
        }

        if (senhaInput.value !== confirmaSenhaInput.value) {
            errorWarning.style.display = "block"; // Exibe o texto de erro
            senhaInput.classList.add("input-error");
            confirmaSenhaInput.classList.add("input-error");
            return false;
        } else {
            errorWarning.style.display = "none"; // Oculta o texto de erro
            senhaInput.classList.remove("input-error");
            confirmaSenhaInput.classList.remove("input-error");
            return true;
        }
    }

    /**
     * Função para alternar a visibilidade do campo de senha de forma genérica
     */
    function togglePassword(inputElement, buttonElement) {
        if (inputElement.type === "password") {
            inputElement.type = "text";
            buttonElement.innerHTML = svgEyeClosed;
        } else {
            inputElement.type = "password";
            buttonElement.innerHTML = svgEyeOpen;
        }
    }

    // Ouvintes de evento (Listeners) para validação em tempo real
    senhaInput.addEventListener("input", verificarSenhas);
    confirmaSenhaInput.addEventListener("input", verificarSenhas);

    // Ouvintes de evento para os botões de mostrar/ocultar senha
    if (btnToggle1) {
        btnToggle1.addEventListener("click", function () {
            togglePassword(senhaInput, btnToggle1);
        });
    }

    if (btnToggle2) {
        btnToggle2.addEventListener("click", function () {
            togglePassword(confirmaSenhaInput, btnToggle2);
        });
    }

    // Impede o envio definitivo do formulário caso o usuário tente burlar as senhas diferentes
    form.addEventListener("submit", function (event) {
        if (!verificarSenhas()) {
            event.preventDefault(); // Cancela o POST do Thymeleaf
        }
    });
});