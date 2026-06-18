document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    // ATENÇÃO: os IDs reais no HTML são "password" e "confirm-password".
    // O th:field="*{senha}" NÃO sobrescreve um id já definido manualmente no input,
    // por isso buscar por "senha"/"confirmacaoSenha" retornava null e quebrava o script.
    const senhaInput = document.getElementById("password");
    const confirmaSenhaInput = document.getElementById("confirm-password");
    const dataNascimentoInput = document.getElementById("dataNascimento");

    // Itens do checklist de força da senha
    const reqMaiuscula = document.getElementById("req-maiuscula");
    const reqMinuscula = document.getElementById("req-minuscula");
    const reqNumero = document.getElementById("req-numero");
    const reqEspecial = document.getElementById("req-especial");

    // 1. MÁSCARA E LIMITE DE 11 NÚMEROS PARA TELEFONE
    telefoneInput.addEventListener("input", function (e) {
        let num = e.target.value.replace(/\D/g, "");
        if (num.length > 11) {
            num = num.substring(0, 11);
        }
        if (num.length > 6) {
            e.target.value = `(${num.substring(0, 2)}) ${num.substring(2, 7)}-${num.substring(7)}`;
        } else if (num.length > 2) {
            e.target.value = `(${num.substring(0, 2)}) ${num.substring(2)}`;
        } else if (num.length > 0) {
            e.target.value = `(${num}`;
        } else {
            e.target.value = "";
        }
    });

    // 2. VERIFICAÇÃO DE FORÇA DA SENHA EM TEMPO REAL (máscara de senha forte)
    senhaInput.addEventListener("input", function (e) {
        atualizarChecklistSenha(e.target.value);

        if (senhaEhForte(e.target.value)) {
            limparErro(senhaInput);
        } else {
            // feedback visual leve enquanto digita, sem bloquear nada aqui
            senhaInput.classList.remove("input-error");
        }
    });

    // 3. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
    form.addEventListener("submit", function (event) {
        let erros = [];

        // Validação do E-mail (precisa ter @ e .)
        const emailValue = emailInput.value;
        if (!emailValue.includes("@") || !emailValue.includes(".")) {
            erros.push("O e-mail inserido é inválido. Certifique-se de que possui '@' e '.'.");
            marcarErro(emailInput);
        } else {
            limparErro(emailInput);
        }

        // Validação da Idade Mínima (Mínimo 14 anos)
        if (dataNascimentoInput.value) {
            const dataNascimento = new Date(dataNascimentoInput.value);
            const hoje = new Date();
            let idade = hoje.getFullYear() - dataNascimento.getFullYear();
            const mes = hoje.getMonth() - dataNascimento.getMonth();

            if (mes < 0 || (mes === 0 && hoje.getDate() < dataNascimento.getDate())) {
                idade--;
            }

            if (idade < 14) {
                erros.push("É necessário ter no mínimo 14 anos para se cadastrar.");
                marcarErro(dataNascimentoInput);
            } else {
                limparErro(dataNascimentoInput);
            }
        }

        // Validação da FORÇA da senha
        let senhaValida = true;
        if (!senhaEhForte(senhaInput.value)) {
            erros.push("A senha deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");
            marcarErro(senhaInput);
            senhaValida = false;
        }

        // Validação das Senhas Iguais
        if (senhaInput.value !== confirmaSenhaInput.value) {
            erros.push("As senhas não coincidem.");
            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
            senhaValida = false;
        } else {
            limparErro(confirmaSenhaInput);
        }

        if (senhaValida) {
            limparErro(senhaInput);
        }

        if (erros.length > 0) {
            event.preventDefault();
            alert(erros.join("\n"));
        }
    });

    // Verifica se a senha possui maiúscula, minúscula, número e caractere especial
    function senhaEhForte(senha) {
        const temMaiuscula = /[A-Z]/.test(senha);
        const temMinuscula = /[a-z]/.test(senha);
        const temNumero = /[0-9]/.test(senha);
        const temEspecial = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);

        return temMaiuscula && temMinuscula && temNumero && temEspecial;
    }

    // Atualiza visualmente o checklist (máscara) de requisitos da senha
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
});