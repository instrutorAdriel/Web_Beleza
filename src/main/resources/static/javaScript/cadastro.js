document.addEventListener("DOMContentLoaded", function () {
    const form = document.querySelector("form");
    const telefoneInput = document.getElementById("telefone");
    const emailInput = document.getElementById("email");
    const senhaInput = document.getElementById("senha");
    const confirmaSenhaInput = document.getElementById("confirmacaoSenha");
    const dataNascimentoInput = document.getElementById("dataNascimento");

    // 1. MÁSCARA E LIMITE DE 11 NÚMEROS PARA TELEFONE
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

    // 2. MÁSCARA PARA DATA DE NASCIMENTO (dd/mm/aaaa)
    dataNascimentoInput.addEventListener("input", function (e) {
        let num = e.target.value.replace(/\D/g, "");
        if (num.length > 8) num = num.substring(0, 8);

        if (num.length > 4) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2, 4)}/${num.substring(4)}`;
        } else if (num.length > 2) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2)}`;
        } else {
            e.target.value = num;
        }
    });

    // 3. VALIDAÇÃO ANTES DE ENVIAR O FORMULÁRIO
    form.addEventListener("submit", function (event) {
        let erros = [];

        // Validação do E-mail
        const emailValue = emailInput.value;
        if (!emailValue.includes("@") || !emailValue.includes(".")) {
            erros.push("O e-mail inserido é inválido. Certifique-se de que possui '@' e '.'.");
            marcarErro(emailInput);
        } else {
            limparErro(emailInput);
        }

        // Validação da Idade Mínima (Mínimo 14 anos)
        if (dataNascimentoInput.value) {
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

        // Validação das Senhas Iguais
        if (senhaInput.value !== confirmaSenhaInput.value) {
            erros.push("As senhas não coincidem.");
            marcarErro(senhaInput);
            marcarErro(confirmaSenhaInput);
        } else {
            limparErro(senhaInput);
            limparErro(confirmaSenhaInput);
        }

        if (erros.length > 0) {
            event.preventDefault();
            alert(erros.join("\n"));
        }
    });

    function marcarErro(input) {
        input.classList.add("input-error");
    }

    function limparErro(input) {
        input.classList.remove("input-error");
    }
});