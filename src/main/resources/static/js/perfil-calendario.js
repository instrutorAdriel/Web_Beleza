document.addEventListener("DOMContentLoaded", () => {
    const campo = document.getElementById("data_nascimento");
    const diaCampo = document.getElementById("data_nascimento_dia");
    const mesCampo = document.getElementById("data_nascimento_mes");
    const anoCampo = document.getElementById("data_nascimento_ano");
    const botao = document.getElementById("abrirCalendarioNascimento");
    const calendario = document.getElementById("calendarioNascimento");
    if (!campo || !diaCampo || !mesCampo || !anoCampo || !botao || !calendario) return;

    const minimo = new Date(1900, 0, 1);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const anoMaximo = hoje.getFullYear();
    const maximo = hoje;
    const mesMaximo = hoje.getMonth() + 1;
    let anoVisual = anoMaximo;
    let mesVisual = mesMaximo - 1;

    const atualizarCampoSubmetido = () => {
        const diaTexto = diaCampo.value;
        const mesTexto = mesCampo.value;
        const anoTexto = anoCampo.value;
        const vazio = !diaTexto && !mesTexto && !anoTexto;
        let data = null;

        if (diaTexto.length === 2 && mesTexto.length === 2 && anoTexto.length === 4) {
            const dia = Number(diaTexto);
            const mes = Number(mesTexto);
            const ano = Number(anoTexto);
            const candidata = new Date(ano, mes - 1, dia);
            if (candidata.getFullYear() === ano && candidata.getMonth() === mes - 1 &&
                candidata.getDate() === dia && candidata >= minimo && candidata <= maximo) {
                data = candidata;
            }
        }

        campo.value = data ? `${diaTexto}/${mesTexto}/${anoTexto}` : "";
        diaCampo.setCustomValidity(vazio || data
            ? "" : "Informe uma data válida entre 01/01/1900 e hoje.");
        diaCampo.setAttribute("aria-invalid", String(!vazio && !data));
    };

    const preencherPartes = data => {
        diaCampo.value = String(data.getDate()).padStart(2, "0");
        mesCampo.value = String(data.getMonth() + 1).padStart(2, "0");
        anoCampo.value = String(data.getFullYear());
        atualizarCampoSubmetido();
    };

    const corrigirDataCompleta = () => {
        if (diaCampo.value.length !== 2 || mesCampo.value.length !== 2 || anoCampo.value.length !== 4) return;
        let dia = Math.max(1, Math.min(Number(diaCampo.value), 31));
        let mes = Math.max(1, Math.min(Number(mesCampo.value), 12));
        let ano = Math.max(minimo.getFullYear(), Math.min(Number(anoCampo.value), anoMaximo));
        dia = Math.min(dia, new Date(ano, mes, 0).getDate());
        let corrigida = new Date(ano, mes - 1, dia);
        if (corrigida > maximo) corrigida = new Date(maximo);
        preencherPartes(corrigida);
    };

    const valorInicial = campo.value;
    const dataInicial = valorInicial.match(/^(\d{2})\/(\d{2})\/(\d{4})$/)
        || valorInicial.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (dataInicial) {
        if (valorInicial.includes("-")) {
            anoCampo.value = dataInicial[1];
            mesCampo.value = dataInicial[2];
            diaCampo.value = dataInicial[3];
        } else {
            diaCampo.value = dataInicial[1];
            mesCampo.value = dataInicial[2];
            anoCampo.value = dataInicial[3];
        }
        atualizarCampoSubmetido();
    }

    [diaCampo, mesCampo, anoCampo].forEach((parte, indice) => {
        parte.addEventListener("input", () => {
            parte.value = parte.value.replace(/\D/g, "").slice(0, Number(parte.maxLength));
            if (parte.value.length === Number(parte.maxLength)) {
                if (parte === diaCampo) parte.value = String(Math.max(1, Math.min(Number(parte.value), 31))).padStart(2, "0");
                if (parte === mesCampo) parte.value = String(Math.max(1, Math.min(Number(parte.value), 12))).padStart(2, "0");
                if (parte === anoCampo) parte.value = String(Math.max(minimo.getFullYear(), Math.min(Number(parte.value), anoMaximo)));
                if (indice < 2) [mesCampo, anoCampo][indice].focus();
                else corrigirDataCompleta();
            }
            atualizarCampoSubmetido();
        });
    });

    const controlesData = diaCampo.closest(".perfil-nascimento-controles");
    controlesData.addEventListener("focusout", event => {
        if ([diaCampo, mesCampo, anoCampo].includes(event.relatedTarget)) return;
        corrigirDataCompleta();
    });

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
        atualizarCampoSubmetido();
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
        preencherPartes(data);
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
