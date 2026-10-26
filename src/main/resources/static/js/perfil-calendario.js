document.addEventListener("DOMContentLoaded", () => {
    const campo = document.getElementById("data_nascimento");
    const botao = document.getElementById("abrirCalendarioNascimento");
    const calendario = document.getElementById("calendarioNascimento");
    if (!campo || !botao || !calendario) return;

    const minimo = new Date(1900, 0, 1);
    const anoMaximo = 2026;
    const maximo = new Date(anoMaximo, 11, 31);
    const mesMaximo = 12;
    let anoVisual = anoMaximo;
    let mesVisual = mesMaximo - 1;

    const dataDoCampo = () => {
        const match = campo.value.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
        if (!match) return null;
        const data = new Date(Number(match[3]), Number(match[2]) - 1, Number(match[1]));
        return data.getFullYear() === Number(match[3]) && data.getMonth() === Number(match[2]) - 1 && data.getDate() === Number(match[1])
            ? data : null;
    };

    campo.addEventListener("input", () => {
        const digitos = campo.value.replace(/\D/g, "").slice(0, 8);
        if (!digitos) {
            campo.value = "";
            return;
        }

        let diaTexto = digitos.slice(0, 2);
        let mesTexto = digitos.slice(2, 4);
        let anoTexto = digitos.slice(4, 8);
        let dia = diaTexto ? Number(diaTexto) : 0;
        let mes = mesTexto ? Number(mesTexto) : 0;
        let ano = anoTexto ? Number(anoTexto) : 0;

        if (diaTexto.length === 2) dia = Math.max(1, Math.min(dia, 31));
        if (mesTexto.length === 2) mes = Math.max(1, Math.min(mes, 12));
        if (anoTexto.length === 4) ano = Math.max(1900, Math.min(ano, anoMaximo));

        if (diaTexto.length === 2 && mesTexto.length === 2) {
            const anoParaCalendario = anoTexto.length === 4 ? ano : 2000;
            dia = Math.min(dia, new Date(anoParaCalendario, mes, 0).getDate());
            if (anoTexto.length === 4) {
                const data = new Date(ano, mes - 1, dia);
                if (data > maximo) {
                    ano = maximo.getFullYear();
                    mes = maximo.getMonth() + 1;
                    dia = maximo.getDate();
                    anoTexto = String(ano);
                    mesTexto = String(mes).padStart(2, "0");
                    diaTexto = String(dia).padStart(2, "0");
                }
            }
        }

        if (diaTexto.length === 2) diaTexto = String(dia).padStart(2, "0");
        if (mesTexto.length === 2) mesTexto = String(mes).padStart(2, "0");
        if (anoTexto.length === 4) anoTexto = String(ano).padStart(4, "0");
        campo.value = diaTexto;
        if (digitos.length > 2) campo.value += `/${mesTexto}`;
        if (digitos.length > 4) campo.value += `/${anoTexto}`;
    });
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
                    `<option value="${mes}" ${mes === mesVisual ? "selected" : ""}>${new Intl.DateTimeFormat("pt-BR", { month: "long" }).format(new Date(2020, mes, 1))}</option>`).join("")}</select></label>
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
