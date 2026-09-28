function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icone = document.getElementById(iconId);

    if (input && icone) {
        if (input.type === "password") {
            input.type = "text";
            icone.classList.replace("fa-eye-slash", "fa-eye");
        } else {
            input.type = "password";
            icone.classList.replace("fa-eye", "fa-eye-slash");
        }
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("kc-form-register") || document.querySelector("form");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
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

    // Regex universal de Emoji e Punycode
    const regexEmoji = /(?:[\uD83C-\uDBFF][\uDC00-\uDFFF]|[\u2600-\u27FF]|\uFE0F)/g;
    const regexPunycode = /xn--[a-zA-Z0-9]+/gi;

    const todosOsCamposTexto = document.querySelectorAll(
        'input[type="text"], input[type="email"], input[type="password"], textarea'
    );

    todosOsCamposTexto.forEach(function(campo) {
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

        if (campo.type === "email" || campo.name === "email" || campo.id === "email") {
            campo.addEventListener('change', function (e) {
                e.target.value = e.target.value.replace(regexPunycode, "").replace(regexEmoji, "");
            });
        }
    });

    // 1. MÁSCARA DE TELEFONE
    if (telefoneInput) {
        telefoneInput.addEventListener("input", function (e) {
            let num = e.target.value.replace(/\D/g, "");
            if (num.length > 11) num = num.substring(0, 11);
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
    }

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

    // 4. MÁSCARA DO NOME COMPLETO
    if (nomeCompletoInput) {
        nomeCompletoInput.addEventListener("input", function (e) {
            const valorOriginal = e.target.value;
            const valorLimpo = valorOriginal.replace(/[^A-Za-zÀ-ÿ\s]/g, "");

            if (valorOriginal !== valorLimpo) {
                if (nomeCompletoErro) nomeCompletoErro.style.display = "flex";
                nomeCompletoInput.classList.add("input-error");
            } else {
                if (nomeCompletoErro) nomeCompletoErro.style.display = "none";
                nomeCompletoInput.classList.remove("input-error");
            }

            e.target.value = valorLimpo;
        });
    }

    // 5. SUBMIT DO FORMULÁRIO
    if (form) {
        form.addEventListener("submit", function (event) {
            let erros = [];

            // Emojis
            todosOsCamposTexto.forEach(function(campo) {
                if (campo && regexEmoji.test(campo.value)) {
                    erros.push(`O campo não pode conter emojis.`);
                    marcarErro(campo);
                }
            });

            // E-mail
            if (emailInput) {
                const emailValue = emailInput.value;
                if (!emailValue.includes("@") || !emailValue.includes(".")) {
                    erros.push("O e-mail inserido é inválido.");
                    marcarErro(emailInput);
                } else {
                    limparErro(emailInput);
                }
            }

            // Data de nascimento para type="date" (formato yyyy-MM-dd)
            if (dataNascimentoInput && dataNascimentoInput.value) {
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
    }

    // ALERTA DE IDADE PARA INPUT TYPE="DATE"
    function atualizarAlertaIdade() {
        if (!dataNascimentoInput || !alertaIdade) return;
        const partes = dataNascimentoInput.value.split("-");

        if (partes.length !== 3 || partes[0].length !== 4) {
            alertaIdade.style.display = "none";
            return;
        }

        const ano = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const dia = parseInt(partes[2], 10);
        const nascimento = new Date(ano, mes, dia);
        const hoje = new Date();

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

    function senhaEhForte(senha) {
        if (!senha) return false;
        return senha.length >= 8 && /[A-Z]/.test(senha) && /[a-z]/.test(senha) && /[0-9]/.test(senha) && /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);
    }

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