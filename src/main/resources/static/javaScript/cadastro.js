document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const dataNascimentoInput = document.getElementById("dataNascimento");

    // 1. MÁSCARA E LIMITE DE 11 NÚMEROS PARA TELEFONE
    telefoneInput.addEventListener("input", function (e) {
        // Remove tudo que não for número
        let num = e.target.value.replace(/\D/g, "");

        // Limita a 11 dígitos
        if (num.length > 11) {
            num = num.substring(0, 11);
        }

        // Aplica a formatação (11) 99999-9999 ou (11) 9999-9999
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

    // 2. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
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

            // Calcula a idade
            let idade = hoje.getFullYear() - dataNascimento.getFullYear();
            const mes = hoje.getMonth() - dataNascimento.getMonth();

            // Ajusta caso o aniversário ainda não tenha acontecido no ano atual
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

        // Validação das Senhas Iguais
        if (senhaInput.value !== confirmaSenhaInput.value) {
            erros.push("As senhas não coincidem.");
            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
        } else {
            limparErro(senhaInput);
            limparErro(confirmaSenhaInput);
        }

        // Se houver algum erro, impede o envio e exibe um alerta
        if (erros.length > 0) {
            event.preventDefault(); // Bloqueia o envio para a Controller Java
            alert(erros.join("\n"));
        }
    });

    // Funções auxiliares para estilização de erro baseadas no seu CSS
    function marcarErro(input) {
        input.classList.add("input-error");
    }

    function limparErro(input) {
        input.classList.remove("input-error");
    }
});