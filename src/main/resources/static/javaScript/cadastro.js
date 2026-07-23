function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icone = document.getElementById(iconId);

    if (input.type === "password") {
        input.type = "text";
        icone.classList.replace("fa-eye-slash", "fa-eye");
    } else {
        input.type = "password";
        icone.classList.replace("fa-eye", "fa-eye-slash");
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.querySelector("form");
    const nomeInput = document.getElementById("nomeCompleto");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("password");
    const confirmaSenhaInput = document.getElementById("confirm-password");
    const dataNascimentoInput = document.getElementById("dataNascimento");
    const enderecoInput = document.getElementById("endereco");
    const alertaIdade = document.getElementById("alertaIdade");

    // Itens do checklist de força da senha
    const reqMaiuscula = document.getElementById("req-maiuscula");
    const reqMinuscula = document.getElementById("req-minuscula");
    const reqNumero = document.getElementById("req-numero");
    const reqEspecial = document.getElementById("req-especial");

    // =======================================================
// 0. BLOQUEIO GLOBAL DE EMOJIS (Compatível com qualquer JS)
// =======================================================

// Captura surroagtes high/low (faixa de emojis) + símbolos comuns + seletores de variação
    // 1. Regex universal para emojis em JS tradicional
    // 1. Regex universal de Emoji
    const regexEmoji = /(?:[\uD83C-\uDBFF][\uDC00-\uDFFF]|[\u2600-\u27FF]|\uFE0F)/g;

// 2. Regex para identificar domínios Punycode (ex: xn--...)
    const regexPunycode = /xn--[a-zA-Z0-9]+/gi;

    const todosOsCamposTexto = document.querySelectorAll(
        'input[type="text"], input[type="email"], input[type="password"], textarea'
    );

    todosOsCamposTexto.forEach(function(campo) {

        // Processa a digitação ao vivo
        campo.addEventListener('input', function (e) {
            const input = e.target;
            const valorOriginal = input.value;
            let valorLimpo = valorOriginal.replace(regexEmoji, "");

            if (input.type === "email" || input.name === "email" || input.id === "email") {
                valorLimpo = valorLimpo.replace(regexPunycode, "");
            }

            if (valorLimpo !== valorOriginal) {
                const posicaoAtual = input.selectionStart;
                const diferencaTamanho = valorOriginal.length - valorLimpo.length;

                input.value = valorLimpo;

                const novaPosicao = Math.max(0, posicaoAtual - diferencaTamanho);
                input.setSelectionRange(novaPosicao, novaPosicao);
            }
        });

        // Limpeza extra para e-mails quando o usuário sai do campo (Garante que o Punycode convertido pelo navegador suma)
        if (campo.type === "email" || campo.name === "email" || campo.id === "email") {
            campo.addEventListener('change', function (e) {
                e.target.value = e.target.value.replace(regexPunycode, "").replace(regexEmoji, "");
            });
        }
    });
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

    // 2. VERIFICAÇÃO DE FORÇA DA SENHA EM TEMPO REAL
    senhaInput.addEventListener("input", function (e) {
        atualizarChecklistSenha(e.target.value);

        if (senhaEhForte(e.target.value)) {
            limparErro(senhaInput);
        } else {
            senhaInput.classList.remove("input-error");
        }
    });

    // 3. MÁSCARA PARA DATA DE NASCIMENTO + ALERTA DE FAIXA ETÁRIA
    dataNascimentoInput.addEventListener("input", function (e) {
        let num = e.target.value.replace(/\D/g, "");
        if (num.length > 8) num = num.substring(0, 8);

        if (num.length >= 2) {
            let dia = parseInt(num.substring(0, 2), 10);
            if (dia > 31) dia = 31;
            num = String(dia).padStart(2, "0") + num.substring(2);
        }

        if (num.length >= 4) {
            let mes = parseInt(num.substring(2, 4), 10);
            if (mes > 12) mes = 12;
            num = num.substring(0, 2) + String(mes).padStart(2, "0") + num.substring(4);
        }

        if (num.length > 4) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2, 4)}/${num.substring(4)}`;
        } else if (num.length > 2) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2)}`;
        } else {
            e.target.value = num;
        }

        atualizarAlertaIdade();
    });

    // 4. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
    form.addEventListener("submit", function (event) {
        let erros = [];

        // Validação Global de Emojis no Submit (Garantia extra)
        camposSemEmoji.forEach(function(campo) {
            if (campo && regexEmoji.test(campo.value)) {
                erros.push(`O campo não pode conter emojis.`);
                marcarErro(campo);
            }
        });

        // Validação do E-mail
        const emailValue = emailInput.value;
        if (!emailValue.includes("@") || !emailValue.includes(".")) {
            erros.push("O e-mail inserido é inválido. Certifique-se de que possui '@' e '.'.");
            marcarErro(emailInput);
        } else {
            limparErro(emailInput);
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
            // Exibe apenas a primeira mensagem de erro se houver emojis duplicados na array
            alert([...new Set(erros)].join("\n"));
        }
    });

    // Calcula a idade e mostra o alerta correspondente
    function atualizarAlertaIdade() {
        const partes = dataNascimentoInput.value.split("/");

        if (partes.length !== 3 || partes[2].length !== 4) {
            alertaIdade.style.display = "none";
            return;
        }

        const dia = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const ano = parseInt(partes[2], 10);
        const nascimento = new Date(ano, mes, dia);
        const hoje = new Date();

        if (nascimento.getFullYear() !== ano || nascimento.getMonth() !== mes || nascimento.getDate() !== dia) {
            alertaIdade.style.display = "none";
            return;
        }

        let idade = hoje.getFullYear() - ano;
        const diffMes = hoje.getMonth() - mes;
        if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < dia)) {
            idade--;
        }

        alertaIdade.className = "ssp-alert";

        if (idade < 8) {
            alertaIdade.classList.add("ssp-alert-erro");
            alertaIdade.textContent = "⚠️ Menores de 8 anos não podem se cadastrar para realizar procedimentos no salão.";
            alertaIdade.style.display = "block";
        } else if (idade < 12) {
            alertaIdade.classList.add("ssp-alert-aviso");
            alertaIdade.textContent = "⚠️ Para se inscrever é preciso ter prévia autorização dos responsáveis legais.";
            alertaIdade.style.display = "block";
        } else if (idade < 18) {
            alertaIdade.classList.add("ssp-alert-info");
            alertaIdade.textContent = "ℹ️ De 12 a 17 anos podem realizar procedimentos sem autorização dos responsáveis.";
            alertaIdade.style.display = "block";
        } else {
            alertaIdade.style.display = "none";
        }
    }

    // Verifica se a senha atende aos requisitos
    function senhaEhForte(senha) {
        const temMaiuscula = /[A-Z]/.test(senha);
        const temMinuscula = /[a-z]/.test(senha);
        const temNumero = /[0-9]/.test(senha);
        const temEspecial = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);

        return temMaiuscula && temMinuscula && temNumero && temEspecial;
    }

    // Atualiza visualmente o checklist
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
        if(input) input.classList.add("input-error");
    }

    function limparErro(input) {
        if(input) input.classList.remove("input-error");
    }
});