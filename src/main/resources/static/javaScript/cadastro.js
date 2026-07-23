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
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    // ATENÇÃO: os IDs reais no HTML são "password" e "confirm-password".
    // O th:field="*{senha}" NÃO sobrescreve um id já definido manualmente no input,
    // por isso buscar por "senha"/"confirmacaoSenha" retornava null e quebrava o script.
    const senhaInput = document.getElementById("password");
    const confirmaSenhaInput = document.getElementById("confirm-password");
    const dataNascimentoInput = document.getElementById("dataNascimento");
<<<<<<< HEAD
=======
    const alertaIdade = document.getElementById("alertaIdade");
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)

    // Itens do checklist de força da senha
    const reqMaiuscula = document.getElementById("req-maiuscula");
    const reqMinuscula = document.getElementById("req-minuscula");
    const reqNumero = document.getElementById("req-numero");
    const reqEspecial = document.getElementById("req-especial");

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

<<<<<<< HEAD
    // 3. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
    // 2. MÁSCARA PARA DATA DE NASCIMENTO (dd/mm/aaaa)
=======
    // 3. MÁSCARA PARA DATA DE NASCIMENTO (dd/mm/aaaa) + ALERTA DE FAIXA ETÁRIA EM TEMPO REAL
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
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
<<<<<<< HEAD
    });

    // 3. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
=======

        atualizarAlertaIdade();
    });

    // 4. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
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

<<<<<<< HEAD
        // Validação da Idade Mínima (Mínimo 14 anos)
        if (dataNascimentoInput.value) {
            const dataNascimento = new Date(dataNascimentoInput.value);
            const hoje = new Date();
            let idade = hoje.getFullYear() - dataNascimento.getFullYear();
            const mes = hoje.getMonth() - dataNascimento.getMonth();

            if (mes < 0 || (mes === 0 && hoje.getDate() < dataNascimento.getDate())) {
                idade--;
            }
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

=======
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
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

<<<<<<< HEAD
=======
    // Calcula a idade e mostra o alerta correspondente enquanto o usuário digita a data
    function atualizarAlertaIdade() {
        const partes = dataNascimentoInput.value.split("/");

        // Só calcula quando a data estiver completa (dd/mm/aaaa)
        if (partes.length !== 3 || partes[2].length !== 4) {
            alertaIdade.style.display = "none";
            return;
        }

        const dia = parseInt(partes[0], 10);
        const mes = parseInt(partes[1], 10) - 1;
        const ano = parseInt(partes[2], 10);
        const nascimento = new Date(ano, mes, dia);
        const hoje = new Date();

        // Data inválida (ex: 31/02) - não mostra alerta de idade
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

>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
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
