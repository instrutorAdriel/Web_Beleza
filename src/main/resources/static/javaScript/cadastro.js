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

    // Itens do checklist de força da senha
    const reqTamanho = document.getElementById("req-tamanho");
    const reqMaiuscula = document.getElementById("req-maiuscula");
    const reqMinuscula = document.getElementById("req-minuscula");
    const reqNumero = document.getElementById("req-numero");
    const reqEspecial = document.getElementById("req-especial");

    // Regex universal de Emoji
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

    // 1. MÁSCARA TELEFONE
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

    // 2. CHECKLIST SENHA
    if (senhaInput) {
        // Executa ao carregar para caso o campo venha preenchido ou com autocomplete
        atualizarChecklistSenha(senhaInput.value);

        senhaInput.addEventListener("input", function (e) {
            atualizarChecklistSenha(e.target.value);
            if (senhaEhForte(e.target.value)) {
                limparErro(senhaInput);
            } else {
                senhaInput.classList.remove("input-error");
            }
        });
    }

    // 3. MÁSCARA DATA DE NASCIMENTO
    if (dataNascimentoInput) {
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

            if (num.length === 8) {
                const anoAtual = new Date().getFullYear();
                let ano = parseInt(num.substring(4, 8), 10);
                if (ano > anoAtual) ano = anoAtual;
                num = num.substring(0, 4) + String(ano).padStart(4, "0");
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
    }

    // 4. MÁSCARA NOME COMPLETO
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

    // 5. SUBMIT E VALIDAÇÃO ESTRITA
    if (form) {
        form.addEventListener("submit", function (event) {
            let erros = [];

            // Validação Emojis
            todosOsCamposTexto.forEach(function(campo) {
                if (campo && regexEmoji.test(campo.value)) {
                    erros.push(`O campo "${campo.name || campo.id}" não pode conter emojis.`);
                    marcarErro(campo);
                }
            });

            // Validação E-mail
            if (emailInput) {
                const emailValue = emailInput.value;
                if (!emailValue.includes("@") || !emailValue.includes(".")) {
                    erros.push("O e-mail inserido é inválido. Certifique-se de que possui '@' e '.'.");
                    marcarErro(emailInput);
                } else {
                    limparErro(emailInput);
                }
            }

            // Validação Idade
            if (dataNascimentoInput && dataNascimentoInput.value) {
                const partes = dataNascimentoInput.value.split("/");

                if (partes.length !== 3 || partes[2].length !== 4) {
                    erros.push("Data de nascimento inválida. Use o formato dd/mm/aaaa.");
                    marcarErro(dataNascimentoInput);
                } else {
                    const dia = parseInt(partes[0], 10);
                    const mes = parseInt(partes[1], 10) - 1;
                    const ano = parseInt(partes[2], 10);
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

            // Validação estrita da Senha (incluindo mínimo de 8 caracteres)
            if (senhaInput) {
                const senhaVal = senhaInput.value;
                if (!senhaEhForte(senhaVal)) {
                    erros.push("A senha deve ter no mínimo 8 caracteres, contendo pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");
                    marcarErro(senhaInput);
                } else {
                    limparErro(senhaInput);
                }
            }

            // Validação de confirmação de senha
            if (senhaInput && confirmaSenhaInput) {
                if (senhaInput.value !== confirmaSenhaInput.value) {
                    erros.push("As senhas não coincidem.");
                    marcarErro(confirmaSenhaInput);
                } else {
                    limparErro(confirmaSenhaInput);
                }
            }

            // SE HOUVER ERRO, BLOQUEIA O ENVIO IMEDIATAMENTE
            if (erros.length > 0) {
                event.preventDefault();
                event.stopPropagation();
                alert(erros.join("\n"));
                return false;
            }
        });
    }

    function atualizarAlertaIdade() {
        if (!dataNascimentoInput || !alertaIdade) return;
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

    function senhaEhForte(senha) {
        if (!senha) return false;
        const temTamanhoMinimo = senha.length >= 8;
        const temMaiuscula = /[A-Z]/.test(senha);
        const temMinuscula = /[a-z]/.test(senha);
        const temNumero = /[0-9]/.test(senha);
        const temEspecial = /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha);

        return temTamanhoMinimo && temMaiuscula && temMinuscula && temNumero && temEspecial;
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