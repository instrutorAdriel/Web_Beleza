function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icone = document.getElementById(iconId);

    if (input.type === "password") {
        input.type = "text";
        // Ao mostrar a senha, coloca o olho riscado
        icone.classList.replace("fa-eye", "fa-eye-slash");
    } else {
        input.type = "password";
        // Ao ocultar a senha, volta ao olho aberto
        icone.classList.replace("fa-eye-slash", "fa-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("kc-form-register") || document.querySelector("form");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    // ATENÇÃO: os IDs reais no HTML são "password" e "confirm-password".
    // O th:field="*{senha}" NÃO sobrescreve um id já definido manualmente no input,
    // por isso buscar por "senha"/"confirmacaoSenha" retornava null e quebrava o script.
    const senhaInput = document.getElementById("password");
    const confirmaSenhaInput = document.getElementById("confirm-password");
    const dataNascimentoInput = document.getElementById("dataNascimento");
    const nomeCompletoInput = document.getElementById("nomeCompleto");
    const nomeCompletoErro = document.getElementById("nomeCompleto-erro");
    const alertaIdade = document.getElementById("alertaIdade");

    // Requisitos do checklist de senha
    const reqTamanho = document.getElementById("req-tamanho");
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
        // Remove tudo que não for número
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

    // 2. CHECKLIST DA SENHA EM TEMPO REAL
    if (senhaInput) {
        senhaInput.addEventListener("input", function (e) {
            atualizarChecklistSenha(e.target.value);
            if (senhaEhForte(e.target.value)) {
                limparErro(senhaInput);
            } else {
                senhaInput.classList.remove("input-error");
            }
        });
    }

    // 3. TRATAMENTO PARA INPUT TYPE="DATE" (NÃO ALTERA O VALOR, SÓ DISPARA O ALERTA)
    if (dataNascimentoInput) {
        dataNascimentoInput.addEventListener("change", function () {
            atualizarAlertaIdade();
        });
        dataNascimentoInput.addEventListener("input", function () {
            atualizarAlertaIdade();
        });
    }

    // 4. BLOQUEIA NÚMEROS E SÍMBOLOS NO NOME COMPLETO (permite letras, acentos e espaços)
    // O aviso fica visível enquanto houver caractere inválido e some quando a pessoa apagar
    nomeCompletoInput.addEventListener("input", function (e) {
        const valorOriginal = e.target.value;
        const valorLimpo = valorOriginal.replace(/[^A-Za-zÀ-ÿ\s]/g, "");

        if (valorOriginal !== valorLimpo) {
            // Havia número ou símbolo digitado — mostra o aviso
            if (nomeCompletoErro) nomeCompletoErro.style.display = "flex";
            nomeCompletoInput.classList.add("input-error");
        } else {
            // Não há mais número/símbolo — esconde o aviso
            if (nomeCompletoErro) nomeCompletoErro.style.display = "none";
            nomeCompletoInput.classList.remove("input-error");
        }

        e.target.value = valorLimpo;
    });

    // 5. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
    form.addEventListener("submit", function (event) {
        let erros = [];

        // Validação Global de Emojis no Submit (Garantia extra)
        todosOsCamposTexto.forEach(function(campo) {
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

        // Validação da Idade Mínima (Mínimo 14 anos)
        // input type="date" entrega o valor em ISO: yyyy-MM-dd
        if (dataNascimentoInput.value) {
            const partes = dataNascimentoInput.value.split("-");

            if (partes.length !== 3 || partes[0].length !== 4) {
                erros.push("Data de nascimento inválida.");
                marcarErro(dataNascimentoInput);
            } else {
                const ano = parseInt(partes[0], 10);
                const mes = parseInt(partes[1], 10) - 1;
                const dia = parseInt(partes[2], 10);
                const dataNascimento = new Date(ano, mes, dia);
                const hoje = new Date();

                if (
                    dataNascimento.getFullYear() !== ano ||
                    dataNascimento.getMonth() !== mes ||
                    dataNascimento.getDate() !== dia
                ) {
                    erros.push("Data de nascimento inválida. Verifique o dia e o mês informados.");
                    marcarErro(dataNascimentoInput);
                } else {
                    let idade = hoje.getFullYear() - dataNascimento.getFullYear();
                    const diffMes = hoje.getMonth() - dataNascimento.getMonth();
                    if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < dataNascimento.getDate())) {
                        idade--;
                    }

                    if (idade < 14) {
                        erros.push("É necessário ter no mínimo 14 anos para se cadastrar.");
                        marcarErro(dataNascimentoInput);
                    } else {
                        limparErro(dataNascimentoInput);
                    }
                }
            }
        }

            // Senha
            if (senhaInput) {
                if (!senhaEhForte(senhaInput.value)) {
                    erros.push("A senha deve cumprir todos os requisitos do checklist.");
                    marcarErro(senhaInput);
                } else {
                    limparErro(senhaInput);
                }
            }

            // Confirmação de Senha
            if (senhaInput && confirmaSenhaInput) {
                if (senhaInput.value !== confirmaSenhaInput.value) {
                    erros.push("As senhas não coincidem.");
                    marcarErro(confirmaSenhaInput);
                } else {
                    limparErro(confirmaSenhaInput);
                }
            }

            if (erros.length > 0) {
                event.preventDefault();
                alert(erros.join("\n"));
            }
        });

    // Calcula a idade e mostra o alerta correspondente conforme o usuário escolhe a data
    // input type="date" entrega o valor em ISO: yyyy-MM-dd
    function atualizarAlertaIdade() {
        const partes = dataNascimentoInput.value.split("-");

        // Só calcula quando a data estiver completa (yyyy-MM-dd)
        if (partes.length !== 3 || partes[0].length !== 4) {
            alertaIdade.style.display = "none";
            return;
        }

        const ano = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const dia = parseInt(partes[2], 10);
        const nascimento = new Date(ano, mes, dia);
        const hoje = new Date();

        // Data inválida - não mostra alerta de idade
        if (nascimento.getFullYear() !== ano || nascimento.getMonth() !== mes || nascimento.getDate() !== dia) {
            alertaIdade.style.display = "none";
            return;
        }

        let idade = hoje.getFullYear() - ano;
        const diffMes = hoje.getMonth() - mes;
        if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < dia)) {
            idade--;
        }

        alertaIdade.className = "ssp-alert"; // reseta as classes de cor antes de reaplicar

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

    // Verifica se a senha possui maiúscula, minúscula, número e caractere especial
    function senhaEhForte(senha) {
        if (!senha) return false;
        return senha.length >= 8 && /[A-Z]/.test(senha) && /[a-z]/.test(senha) && /[0-9]/.test(senha) && /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);
    }

    // Atualiza visualmente o checklist (máscara) de requisitos da senha
    function atualizarChecklistSenha(senha) {
        const regras = [
            { elemento: reqTamanho, valida: (s) => s.length >= 8 },
            { elemento: reqMaiuscula, valida: (s) => /[A-Z]/.test(s) },
            { elemento: reqMinuscula, valida: (s) => /[a-z]/.test(s) },
            { elemento: reqNumero, valida: (s) => /[0-9]/.test(s) },
            { elemento: reqEspecial, valida: (s) => /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(s) }
        ];

        regras.forEach(({ elemento, valida }) => {
            if (!elemento) return;
            const icone = elemento.querySelector(".req-icon");
            if (valida(senha)) {
                elemento.classList.add("valid");
                if (icone) {
                    icone.classList.remove("fa-circle-xmark");
                    icone.classList.add("fa-circle-check");
                }
            } else {
                elemento.classList.remove("valid");
                if (icone) {
                    icone.classList.remove("fa-circle-check");
                    icone.classList.add("fa-circle-xmark");
                }
            }
        });
    }

    function marcarErro(input) {
        if (input) input.classList.add("input-error");
    }

    function limparErro(input) {
        if (input) input.classList.remove("input-error");
    }

});