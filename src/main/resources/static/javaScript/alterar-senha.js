document.addEventListener("DOMContentLoaded", function () {

    // Seleção dos elementos do formulário e de validação
    const form = document.getElementById("form-alterar-senha");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const errorWarning = document.getElementById("password-error");

    // Seleção dos botões de alternar senha e de seus respectivos ícones
    const btnToggle1 = document.getElementById("btnToggle1");
    const btnToggle2 = document.getElementById("btnToggle2");
    const eyeIcon1 = document.getElementById("eyeIcon1");
    const eyeIcon2 = document.getElementById("eyeIcon2");

    /**
     * Verifica se a senha atende aos requisitos
     */
    function senhaEhForte(senha) {
        const temMaiuscula = /[A-Z]/.test(senha);
        const temMinuscula = /[a-z]/.test(senha);
        const temNumero = /[0-9]/.test(senha);
        const temEspecial = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);

        return temMaiuscula &&
            temMinuscula &&
            temNumero &&
            temEspecial;
    }

    /**
     * Valida senha forte e igualdade entre as senhas
     */
    function verificarSenhas() {

        // Limpa erros
        errorWarning.style.display = "none";
        senhaInput.classList.remove("input-error");
        confirmaSenhaInput.classList.remove("input-error");

        // Não faz nada enquanto confirmação estiver vazia
        if (confirmaSenhaInput.value === "") {
            return true;
        }

        // Verifica força da senha
        if (!senhaEhForte(senhaInput.value)) {
            errorWarning.innerText =
                "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.";

            errorWarning.style.display = "block";
            senhaInput.classList.add("input-error");

            return false;
        }

        // Verifica se as senhas são iguais
        if (senhaInput.value !== confirmaSenhaInput.value) {

            errorWarning.innerText = "As senhas não coincidem.";

            errorWarning.style.display = "block";
            senhaInput.classList.add("input-error");
            confirmaSenhaInput.classList.add("input-error");

            return false;
        }

        return true;
    }

    /**
     * Alterna entre mostrar e ocultar senha
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

    // Validação em tempo real
    senhaInput.addEventListener("input", verificarSenhas);
    confirmaSenhaInput.addEventListener("input", verificarSenhas);

    // Botões de visualizar senha
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

    // Validação antes de enviar o formulário
    form.addEventListener("submit", function (event) {

        let erros = [];

        if (!senhaEhForte(senhaInput.value)) {
            erros.push("A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");
            senhaInput.classList.add("input-error");
        }

        if (senhaInput.value !== confirmaSenhaInput.value) {
            erros.push("As senhas não coincidem.");
            senhaInput.classList.add("input-error");
            confirmaSenhaInput.classList.add("input-error");
        }

        if (erros.length > 0) {
            event.preventDefault();
            alert(erros.join("\n"));
        }
    });

});