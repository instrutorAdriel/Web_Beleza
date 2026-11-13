function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icone = document.getElementById(iconId);

    if (input.type === "password") {
        input.type = "text";
        icone.classList.replace("fa-eye", "fa-eye-slash");
    } else {
        input.type = "password";
        icone.classList.replace("fa-eye-slash", "fa-eye");
    }
}

document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("kc-form-register") || document.querySelector("form");
    const btnSubmit = document.getElementById("btnSubmitCadastro");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("password");
    const confirmaSenhaInput = document.getElementById("confirm-password");
    const dataNascimentoInput = document.getElementById("data_nascimento");
    const nomeCompletoInput = document.getElementById("nomeCompleto");
    const alertaIdade = document.getElementById("alertaIdade");

    const dominiosPessoais = new Set([
        "gmail.com", "googlemail.com", "hotmail.com", "hotmail.com.br", "outlook.com",
        "outlook.com.br", "live.com", "live.com.br", "msn.com", "yahoo.com", "yahoo.com.br",
        "icloud.com", "me.com", "mac.com", "aol.com", "proton.me", "protonmail.com",
        "uol.com.br", "bol.com.br", "terra.com.br", "ig.com.br", "zoho.com", "gmx.com",
        "gmx.net", "fastmail.com", "hey.com"
    ]);

    const regexEmoji = /(?:[\uD83C-\uDBFF][\uDC00-\uDFFF]|[\u2600-\u27FF]|\uFE0F)/g;
    const regexPunycode = /xn--[a-zA-Z0-9]+/gi;

    const todosOsCamposTexto = document.querySelectorAll(
        'input[type="text"], input[type="email"], input[type="password"], input[type="tel"], textarea'
    );

    todosOsCamposTexto.forEach(function(campo) {
        campo.addEventListener("input", function (e) {
            const input = e.target;
            const valorOriginal = input.value;
            let valorLimpo = valorOriginal.replace(regexEmoji, "");

            if (input.type === "email" || input.id === "email") {
                valorLimpo = valorLimpo.replace(regexPunycode, "");
            }

            if (valorLimpo !== valorOriginal) {
                const pos = input.selectionStart;
                const diff = valorOriginal.length - valorLimpo.length;
                input.value = valorLimpo;
                input.setSelectionRange(Math.max(0, pos - diff), Math.max(0, pos - diff));
            }
        });
    });

    if (nomeCompletoInput) {
        nomeCompletoInput.addEventListener("input", function (e) {
            const valorOriginal = e.target.value;
            const valorLimpo = valorOriginal.replace(/[^A-Za-zÀ-ÿ\s]/g, "");
            e.target.value = valorLimpo;

            if (valorOriginal !== valorLimpo) {
                mostrarErroCampo(nomeCompletoInput, "nomeCompleto-erro", "Não é permitido digitar números ou símbolos no nome.");
            } else {
                limparErroCampo(nomeCompletoInput, "nomeCompleto-erro");
            }
        });
    }

    if (telefoneInput) {
        telefoneInput.addEventListener("input", function (e) {
            let num = e.target.value.replace(/\D/g, "").substring(0, 11);
            if (num.length > 7) {
                e.target.value = `(${num.substring(0, 2)}) ${num.substring(2, 7)}-${num.substring(7)}`;
            } else if (num.length > 2) {
                e.target.value = `(${num.substring(0, 2)}) ${num.substring(2)}`;
            } else if (num.length > 0) {
                e.target.value = `(${num}`;
            } else {
                e.target.value = "";
            }
            limparErroCampo(telefoneInput, "telefone-erro");
        });
    }

    if (dataNascimentoInput) {
        const hojeISO = new Date().toISOString().split("T")[0];
        dataNascimentoInput.setAttribute("max", hojeISO);

        const aoMudarData = () => {
            atualizarAlertaIdade();
            limparErroCampo(dataNascimentoInput, "data_nascimento-erro");
        };

        dataNascimentoInput.addEventListener("change", aoMudarData);
        dataNascimentoInput.addEventListener("input", aoMudarData);
    }

    if (emailInput) {
        emailInput.addEventListener("input", () => limparErroCampo(emailInput, "email-erro"));
    }
    if (senhaInput) {
        senhaInput.addEventListener("input", () => limparErroCampo(senhaInput, "password-erro"));
    }
    if (confirmaSenhaInput) {
        confirmaSenhaInput.addEventListener("input", () => limparErroCampo(confirmaSenhaInput, "confirm-password-erro"));
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        let primeiroCampoInvalido = null;
        const registrarErro = (elementoVisual, erroId, mensagem, elementoFoco = elementoVisual) => {
            mostrarErroCampo(elementoVisual, erroId, mensagem);
            if (!primeiroCampoInvalido) primeiroCampoInvalido = elementoFoco;
        };

        limparErroCampo(nomeCompletoInput, "nomeCompleto-erro");
        limparErroCampo(dataNascimentoInput, "data_nascimento-erro")
        limparErroCampo(telefoneInput, "telefone-erro");
        limparErroCampo(emailInput, "email-erro");
        limparErroCampo(senhaInput, "password-erro");
        limparErroCampo(confirmaSenhaInput, "confirm-password-erro");

        const nome = nomeCompletoInput.value.trim();
        if (nome.length < 3 || !nome.includes(" ")) {
            registrarErro(nomeCompletoInput, "nomeCompleto-erro", "Digite seu nome e sobrenome.");
        }

        const nascimento = parseDataISO(dataNascimentoInput.value);
        if (!nascimento) {
            registrarErro(dataNascimentoInput, "data_nascimento-erro", "Informe uma data de nascimento válida.");
        } else {
            const idade = calcularIdade(nascimento);
            if (nascimento.getFullYear() < 1900 || idade < 0) {
                registrarErro(dataNascimentoInput, "data_nascimento-erro", "Data de nascimento inválida.");
            } else if (idade < 14) {
                registrarErro(dataNascimentoInput, "data_nascimento-erro", "É necessário ter no mínimo 14 anos para se cadastrar.");
            }
        }

        const digitosTelefone = telefoneInput.value.replace(/\D/g, "");
        if (digitosTelefone.length < 10 || digitosTelefone.length > 11) {
            registrarErro(telefoneInput, "telefone-erro", "Informe um telefone válido com DDD.");
        }

        const senha = senhaInput.value;
        if (!senhaEhForte(senha)) {
            registrarErro(
                senhaInput,
                "password-erro",
                "A senha deve ter no mínimo 8 caracteres, com letra maiúscula, minúscula, número e caractere especial."
            );
        }

        if (!confirmaSenhaInput.value || senha !== confirmaSenhaInput.value) {
            registrarErro(confirmaSenhaInput, "confirm-password-erro", "As senhas não coincidem.");
        }

        const email = emailInput.value.trim().toLowerCase();
        const partesEmail = email.split("@");
        const formatoValido =
            partesEmail.length === 2 &&
            /^[^\s@]+$/.test(partesEmail[0]) &&
            /^[a-z0-9.-]+\.[a-z]{2,}$/i.test(partesEmail[1]);
        const provedorPessoal = formatoValido && dominiosPessoais.has(partesEmail[1]);

        if (!formatoValido || !provedorPessoal) {
            registrarErro(
                emailInput,
                "email-erro",
                "Informe um e-mail pessoal válido (ex: Gmail, Hotmail, Outlook)."
            );
        } else {
            if (btnSubmit) {
                btnSubmit.disabled = true;
                btnSubmit.textContent = "Verificando...";
            }
            try {
                const resposta = await fetch("/validar-email-cadastro?email=" + encodeURIComponent(email));
                const dominioAtivo = resposta.ok && (await resposta.json());
                if (!dominioAtivo) {
                    registrarErro(emailInput, "email-erro", "Este domínio de e-mail não está ativo ou não existe.");
                }
            } catch (e) {
                // Em caso de falha de rede, deixa o backend validar no POST
            } finally {
                if (btnSubmit) {
                    btnSubmit.disabled = false;
                    btnSubmit.textContent = "Finalizar Cadastro";
                }
            }
        }

        if (primeiroCampoInvalido) {
            primeiroCampoInvalido.scrollIntoView({ behavior: "smooth", block: "center" });
            primeiroCampoInvalido.focus();
            return;
        }

        form.submit();
    });

    function mostrarErroCampo(elementoVisual, erroId, mensagem) {
        if (elementoVisual) {
            elementoVisual.classList.add("input-error");
        }
        const smallErro = document.getElementById(erroId);
        if (smallErro) {
            const span = smallErro.querySelector("span");
            if (span) span.textContent = mensagem;
            smallErro.style.display = "flex";
        }
    }

    function limparErroCampo(elementoVisual, erroId) {
        if (elementoVisual) {
            elementoVisual.classList.remove("input-error");
        }
        const smallErro = document.getElementById(erroId);
        if (smallErro) {
            smallErro.style.display = "none";
        }
    }

    function senhaEhForte(senha) {
        if (!senha) return false;
        return (
            senha.length >= 8 &&
            /[A-Z]/.test(senha) &&
            /[a-z]/.test(senha) &&
            /[0-9]/.test(senha) &&
            /[!@#$%^&*(),.?":{}|<>_\-+=\[\]\\/;'`~]/.test(senha)
        );
    }

    function parseDataISO(valor) {
        if (!valor || !/^\d{4}-\d{2}-\d{2}$/.test(valor)) return null;
        const [ano, mes, dia] = valor.split("-").map(Number);
        const data = new Date(ano, mes - 1, dia);
        if (data.getFullYear() !== ano || data.getMonth() !== mes - 1 || data.getDate() !== dia) {
            return null;
        }
        return data;
    }

    function calcularIdade(nascimento) {
        const hoje = new Date();
        let idade = hoje.getFullYear() - nascimento.getFullYear();
        const diffMes = hoje.getMonth() - nascimento.getMonth();
        if (diffMes < 0 || (diffMes === 0 && hoje.getDate() < nascimento.getDate())) {
            idade--;
        }
        return idade;
    }

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

    /*
    function atualizarChecklistEmail() {
        const email = emailInput.value.trim().toLowerCase();
        const partes = email.split("@");
        const formatoValido = partes.length === 2 && /^[^\s@]+$/.test(partes[0]) && /^[a-z0-9.-]+\.[a-z]{2,}$/i.test(partes[1]);
        const provedorPessoal = formatoValido && dominiosPessoais.has(partes[1]);
        atualizarItemChecklist(emailReqFormato, formatoValido);
        atualizarItemChecklist(emailReqPessoal, provedorPessoal);
        emailDominioVerificado = false;
        atualizarItemChecklist(emailReqDominio, false);

        clearTimeout(emailVerificacaoTimer);
        if (!provedorPessoal) return;

        emailReqDominio.querySelector(".req-icon").className = "fa-solid fa-spinner fa-spin req-icon";
        emailReqDominio.lastChild.textContent = " Verificando domínio...";
        emailVerificacaoTimer = setTimeout(async function () {
            try {
                const resposta = await fetch("/validar-email-cadastro?email=" + encodeURIComponent(email));
                emailDominioVerificado = resposta.ok && await resposta.json();
            } catch (erro) {
                emailDominioVerificado = false;
            }
            atualizarItemChecklist(emailReqDominio, emailDominioVerificado);
            emailReqDominio.lastChild.textContent = emailDominioVerificado
                ? " Domínio com serviço de e-mail ativo"
                : " Domínio sem serviço de e-mail ativo";
        }, 500);
    }

    function atualizarItemChecklist(elemento, valido) {
        if (!elemento) return;
        const icone = elemento.querySelector(".req-icon");
        elemento.classList.toggle("valid", valido);
        if (icone) {
            icone.classList.toggle("fa-circle-check", valido);
            icone.classList.toggle("fa-circle-xmark", !valido);
            icone.classList.remove("fa-spinner", "fa-spin");
        }
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
*/
});
