document.addEventListener("DOMContentLoaded", () => {
    const campo = document.getElementById("data_nascimento");
    const botao = document.getElementById("abrirCalendarioNascimento");
    const calendario = document.getElementById("calendarioNascimento");
    if (!campo || !botao || !calendario) return;

    const minimo = new Date(1900, 0, 1);
    const hoje = new Date();
    const anoMaximo = hoje.getFullYear();
    const mesMaximo = hoje.getMonth() + 1;
    const diaMaximo = hoje.getDate();
    const maximo = new Date(anoMaximo, mesMaximo - 1, diaMaximo);
    let anoVisual = anoMaximo;
    let mesVisual = mesMaximo - 1;

    const dataDoCampo = () => {
        const match = campo.value.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
        if (!match) return null;
        const data = new Date(Number(match[3]), Number(match[2]) - 1, Number(match[1]));
        return data.getFullYear() === Number(match[3]) && data.getMonth() === Number(match[2]) - 1 && data.getDate() === Number(match[1])
            ? data : null;
    };
    const fechar = () => {
        calendario.hidden = true;
        botao.setAttribute("aria-expanded", "false");
    };

    const renderizar = () => {
        const dataMes = new Date(anoVisual, mesVisual, 1);
        const nomeMes = new Intl.DateTimeFormat("pt-BR", { month: "long" }).format(dataMes);
        const anos = Array.from({ length: anoMaximo - minimo.getFullYear() + 1 }, (_, i) => anoMaximo - i)
            .map(ano => `<option value="${ano}" ${ano === anoVisual ? "selected" : ""}>${ano}</option>`).join("");
        const diasSemana = ["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"];
        const deslocamento = (dataMes.getDay() + 6) % 7;
        const diasNoMes = new Date(anoVisual, mesVisual + 1, 0).getDate();
        const selecionada = dataDoCampo();
        const celulas = Array.from({ length: deslocamento }, () => '<span aria-hidden="true"></span>');

        for (let dia = 1; dia <= diasNoMes; dia++) {
            const data = new Date(anoVisual, mesVisual, dia);
            const desabilitada = data < minimo || data > maximo;
            const ativa = selecionada && data.getTime() === selecionada.getTime();
            celulas.push(`<button type="button" data-dia="${dia}" ${desabilitada ? "disabled" : ""} class="${ativa ? "selecionado" : ""}" aria-pressed="${Boolean(ativa)}">${dia}</button>`);
        }

        calendario.innerHTML = `
            <div class="perfil-calendario-seletores">
                <label>Mês<select data-mes aria-label="Mês">${Array.from({ length: 12 }, (_, mes) =>
                    `<option value="${mes}" ${mes === mesVisual ? "selected" : ""} ${anoVisual === anoMaximo && mes > mesMaximo - 1 ? "disabled" : ""}>${new Intl.DateTimeFormat("pt-BR", { month: "long" }).format(new Date(2020, mes, 1))}</option>`).join("")}</select></label>
                <label>Ano<select data-ano aria-label="Ano">${anos}</select></label>
                <span aria-live="polite">${nomeMes} de ${anoVisual}</span>
            </div>
            <div class="perfil-calendario-grade" role="grid">
                ${diasSemana.map(dia => `<span class="perfil-calendario-dia-semana">${dia}</span>`).join("")}
                ${celulas.join("")}
            </div>`;
    };

    botao.addEventListener("click", () => {
        const selecionada = dataDoCampo();
        anoVisual = selecionada ? selecionada.getFullYear() : anoMaximo;
        mesVisual = selecionada ? selecionada.getMonth() : mesMaximo - 1;
        renderizar();
        calendario.hidden = false;
        botao.setAttribute("aria-expanded", "true");
    });

    calendario.addEventListener("change", event => {
        if (event.target.matches("[data-ano]")) anoVisual = Number(event.target.value);
        if (event.target.matches("[data-mes]")) mesVisual = Number(event.target.value);
        if (anoVisual === anoMaximo && mesVisual > mesMaximo - 1) mesVisual = mesMaximo - 1;
        renderizar();
    });

    calendario.addEventListener("click", event => {
        const alvo = event.target.closest("[data-dia]");
        if (!alvo || alvo.disabled) return;
        const data = new Date(anoVisual, mesVisual, Number(alvo.dataset.dia));
        campo.value = `${String(data.getDate()).padStart(2, "0")}/${String(data.getMonth() + 1).padStart(2, "0")}/${data.getFullYear()}`;
        campo.dispatchEvent(new Event("change", { bubbles: true }));
        fechar();
    });

    document.addEventListener("click", event => {
        if (!calendario.contains(event.target) && !botao.contains(event.target)) fechar();
    });
    document.addEventListener("keydown", event => {
        if (event.key === "Escape") fechar();
    });
});
