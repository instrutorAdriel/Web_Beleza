document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("form-alterar-senha");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const errorWarning = document.getElementById("password-error");

    const btnToggle1 = document.getElementById("btnToggle1");
    const btnToggle2 = document.getElementById("btnToggle2");
    const eyeIcon1 = document.getElementById("eyeIcon1");
    const eyeIcon2 = document.getElementById("eyeIcon2");

    // Checklist
    const reqMaiuscula = document.getElementById("req-maiuscula");
    const reqMinuscula = document.getElementById("req-minuscula");
    const reqNumero = document.getElementById("req-numero");
    const reqEspecial = document.getElementById("req-especial");

    const REGEX_ESPECIAL = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/;
    const MSG_SENHA_FRACA = "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.";
    const MSG_NAO_COINCIDEM = "As senhas não coincidem.";

    function senhaEhForte(senha) {
        return /[A-Z]/.test(senha) &&
            /[a-z]/.test(senha) &&
            /[0-9]/.test(senha) &&
            REGEX_ESPECIAL.test(senha);
    }

    function atualizarChecklistSenha(senha) {
        const regras = [
            { elemento: reqMaiuscula, regex: /[A-Z]/ },
            { elemento: reqMinuscula, regex: /[a-z]/ },
            { elemento: reqNumero, regex: /[0-9]/ },
            { elemento: reqEspecial, regex: REGEX_ESPECIAL }
        ];

        regras.forEach(({ elemento, regex }) => {
            if (!elemento) return;

            const icone = elemento.querySelector(".req-icon");
            const valido = regex.test(senha);

            elemento.classList.toggle("valid", valido);

            if (icone) {
                icone.classList.toggle("fa-circle-check", valido);
                icone.classList.toggle("fa-circle-xmark", !valido);
            }
        });
    }

    function marcarErro(input) {
        input.classList.add("input-error");
    }

    function limparErro(input) {
        input.classList.remove("input-error");
    }

    function mostrarErro(mensagem) {
        errorWarning.innerHTML = mensagem;
        errorWarning.style.display = "block";
    }

    function esconderErro() {
        errorWarning.innerHTML = "";
        errorWarning.style.display = "none";
    }

    function verificarSenhas() {
        atualizarChecklistSenha(senhaInput.value);

        limparErro(senhaInput);
        limparErro(confirmaSenhaInput);
        esconderErro();

        if (senhaInput.value !== "" && !senhaEhForte(senhaInput.value)) {
            mostrarErro(MSG_SENHA_FRACA);
            marcarErro(senhaInput);
            return false;
        }

        if (confirmaSenhaInput.value !== "" &&
            senhaInput.value !== confirmaSenhaInput.value) {
            mostrarErro(MSG_NAO_COINCIDEM);
            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
            return false;
        }

        return true;
    }

    function togglePasswordVisibility(inputElement, iconElement) {
        const mostrando = inputElement.type === "password";
        inputElement.type = mostrando ? "text" : "password";
        iconElement.classList.toggle("fa-eye", mostrando);
        iconElement.classList.toggle("fa-eye-slash", !mostrando);
    }

    senhaInput.addEventListener("input", verificarSenhas);
    confirmaSenhaInput.addEventListener("input", verificarSenhas);

    btnToggle1.addEventListener("click", function () {
        togglePasswordVisibility(senhaInput, eyeIcon1);
    });

    btnToggle2.addEventListener("click", function () {
        togglePasswordVisibility(confirmaSenhaInput, eyeIcon2);
    });

    form.addEventListener("submit", function (event) {
        const erros = [];

        atualizarChecklistSenha(senhaInput.value);

        if (!senhaEhForte(senhaInput.value)) {
            erros.push(MSG_SENHA_FRACA);
            marcarErro(senhaInput);
        }

        if (senhaInput.value !== confirmaSenhaInput.value) {
            erros.push(MSG_NAO_COINCIDEM);
            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
        }

        if (erros.length > 0) {
            event.preventDefault();
            mostrarErro(erros.join("<br>"));
        }
    });

});