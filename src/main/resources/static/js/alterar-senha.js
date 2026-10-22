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

    function atualizarChecklistSenha(senha) {

        const regras = [
            { elemento: reqMaiuscula, regex: /[A-Z]/ },
            { elemento: reqMinuscula, regex: /[a-z]/ },
            { elemento: reqNumero, regex: /[0-9]/ },
            { elemento: reqEspecial, regex: /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/ }
        ];

        regras.forEach(({ elemento, regex }) => {

            if (!elemento) return;

            const icone = elemento.querySelector(".req-icon");

            if (regex.test(senha)) {
                elemento.classList.add("valid");
                icone.classList.remove("fa-circle-xmark");
                icone.classList.add("fa-circle-check");
            } else {
                elemento.classList.remove("valid");
                icone.classList.remove("fa-circle-check");
                icone.classList.add("fa-circle-xmark");
            }
        });
    }

    function marcarErro(input) {
        input.classList.add("input-error");
    }

    function limparErro(input) {
        input.classList.remove("input-error");
    }

    function verificarSenhas() {

        atualizarChecklistSenha(senhaInput.value);

        limparErro(senhaInput);
        limparErro(confirmaSenhaInput);

        errorWarning.style.display = "none";

        if (senhaInput.value !== "" && !senhaEhForte(senhaInput.value)) {

            errorWarning.innerText =
                "A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.";

            errorWarning.style.display = "block";

            marcarErro(senhaInput);

            return false;
        }

        if (confirmaSenhaInput.value !== "" &&
            senhaInput.value !== confirmaSenhaInput.value) {

            errorWarning.innerText = "As senhas não coincidem.";

            errorWarning.style.display = "block";

            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);

            return false;
        }

        return true;
    }

    function togglePasswordVisibility(inputElement, iconElement) {

        if (inputElement.type === "password") {
            inputElement.type = "text";
            iconElement.classList.replace("fa-eye-slash", "fa-eye");
        } else {
            inputElement.type = "password";
            iconElement.classList.replace("fa-eye", "fa-eye-slash");
        }
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

        let erros = [];

        atualizarChecklistSenha(senhaInput.value);

        if (!senhaEhForte(senhaInput.value)) {

            erros.push("A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");

            marcarErro(senhaInput);
        }

        if (senhaInput.value !== confirmaSenhaInput.value) {

            erros.push("As senhas não coincidem.");

            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
        }

        if (erros.length > 0) {

            event.preventDefault();

            errorWarning.innerHTML = erros.join("<br>");
            errorWarning.style.display = "block";

            alert(erros.join("\n"));
        }
    });

});