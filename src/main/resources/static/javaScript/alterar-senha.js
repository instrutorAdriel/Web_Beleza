document.addEventListener("DOMContentLoaded", function () {
    // Seleção dos elementos do formulário e de validação
    const form = document.getElementById("form-alterar-senha");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const errorWarning = document.getElementById("password-error");

    // Seleção dos botões de alternar senha e de seus respectivos ícones (FontAwesome)
    const btnToggle1 = document.getElementById("btnToggle1");
    const btnToggle2 = document.getElementById("btnToggle2");
    const eyeIcon1 = document.getElementById("eyeIcon1");
    const eyeIcon2 = document.getElementById("eyeIcon2");

    /**
     * Função que valida se os dois campos de senha são idênticos
     */
    function verificarSenhas() {
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
     * Função baseada na tela de Cadastro para alternar ícone do FontAwesome e visibilidade
     */
    function togglePasswordVisibility(inputElement, iconElement) {
        if (inputElement.type === "password") {
            inputElement.type = "text";
            iconElement.classList.replace("fa-eye-slash", "fa-eye");
        } else {
            inputElement.type = "password";
            iconElement.classList.replace("fa-eye", "fa-eye-slash");
        }
    }

    // Ouvintes de evento para validação em tempo real das senhas
    senhaInput.addEventListener("input", verificarSenhas);
    confirmaSenhaInput.addEventListener("input", verificarSenhas);

    // Ouvintes de evento para os botões usando a nova lógica do ícone
    if (btnToggle1 && eyeIcon1) {
        btnToggle1.addEventListener("click", function () {
            togglePasswordVisibility(senhaInput, eyeIcon1);
        });
    }

    if (btnToggle2 && eyeIcon2) {
        btnToggle2.addEventListener("click", function () {
            togglePasswordVisibility(confirmaSenhaInput, eyeIcon2);
        });
    }

    // Impede o envio definitivo do formulário caso o usuário tente burlar as senhas diferentes
    form.addEventListener("submit", function (event) {
        if (!verificarSenhas()) {
            event.preventDefault(); // Cancela o POST do Thymeleaf
        }
    });
});