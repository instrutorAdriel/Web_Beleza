document.addEventListener("DOMContentLoaded", () => {
    const campoData = document.getElementById("dataNascimento");
    const botaoCalendario = document.getElementById("abrirCalendarioNascimento");
    const calendario = document.getElementById("calendarioNascimento");
    if (!campoData || !botaoCalendario || !calendario) return;

    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    const anoAtual = hoje.getFullYear();
    const dataMinima = new Date(1900, 0, 1);
    let mesVisivel = new Date(anoAtual, hoje.getMonth(), 1);
    let modo = "dias";

    const lerData = (valor) => {
        const partes = valor.trim().match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
        if (!partes) return null;
        const data = new Date(Number(partes[3]), Number(partes[2]) - 1, Number(partes[1]));
        if (data.getFullYear() !== Number(partes[3]) || data.getMonth() !== Number(partes[2]) - 1 || data.getDate() !== Number(partes[1])) return null;
        return data;
    };

    const mesmaData = (a, b) => a && b && a.getFullYear() === b.getFullYear() && a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

    const renderizar = () => {
        const ano = mesVisivel.getFullYear();
        const mes = mesVisivel.getMonth();

        if (modo === "anos") {
            calendario.innerHTML = `
                <div class="perfil-calendario-cabecalho perfil-calendario-cabecalho-anos">
                    <strong>Escolha o ano</strong>
                    <button type="button" data-voltar-meses>Voltar</button>
                </div>
                <div class="perfil-calendario-anos">
                    ${Array.from({ length: anoAtual - 1899 }, (_, indice) => anoAtual - indice).map(anoOpcao =>
                        `<button type="button" class="perfil-calendario-ano ${anoOpcao === ano ? "selecionado" : ""}" data-ano="${anoOpcao}">${anoOpcao}</button>`
                    ).join("")}
                </div>`;
            calendario.querySelector(`[data-ano="${ano}"]`)?.scrollIntoView({ block: "center" });
            return;
        }

        const primeiroDia = new Date(ano, mes, 1);
        const deslocamento = (primeiroDia.getDay() + 6) % 7;
        const diasNoMes = new Date(ano, mes + 1, 0).getDate();
        const selecionada = lerData(campoData.value);
        const nomeMes = new Intl.DateTimeFormat("pt-BR", { month: "long" }).format(primeiroDia);

        calendario.innerHTML = `
            <div class="perfil-calendario-cabecalho">
                <button type="button" data-mover="-1" aria-label="Mês anterior">&#8249;</button>
                <strong><span>${nomeMes} de </span><button type="button" class="perfil-calendario-ano-atual" data-mostrar-anos aria-label="Escolher ano">${ano}</button></strong>
                <button type="button" data-mover="1" aria-label="Próximo mês">&#8250;</button>
            </div>
            <div class="perfil-calendario-grade" role="grid">
                ${["Seg", "Ter", "Qua", "Qui", "Sex", "Sáb", "Dom"].map(dia => `<span class="perfil-calendario-dia-semana">${dia}</span>`).join("")}
                ${Array.from({ length: deslocamento }, () => '<span class="perfil-calendario-vazio"></span>').join("")}
                ${Array.from({ length: diasNoMes }, (_, indice) => {
                    const data = new Date(ano, mes, indice + 1);
                    const desabilitada = data < dataMinima || data > hoje;
                    const classes = ["perfil-calendario-dia", mesmaData(data, hoje) ? "hoje" : "", mesmaData(data, selecionada) ? "selecionado" : ""].filter(Boolean).join(" ");
                    return `<button type="button" class="${classes}" data-dia="${indice + 1}" ${desabilitada ? "disabled" : ""}>${indice + 1}</button>`;
                }).join("")}
            </div>`;
    };

    calendario.addEventListener("click", (evento) => {
        const alvo = evento.target.closest("button");
        if (!alvo) return;
        evento.preventDefault();
        evento.stopPropagation();

        if (alvo.hasAttribute("data-mover")) {
            const proximoMes = new Date(mesVisivel.getFullYear(), mesVisivel.getMonth() + Number(alvo.dataset.mover), 1);
            if (proximoMes <= new Date(anoAtual, hoje.getMonth(), 1) && proximoMes >= dataMinima) {
                mesVisivel = proximoMes;
                renderizar();
            }
        } else if (alvo.hasAttribute("data-mostrar-anos")) {
            modo = "anos";
            renderizar();
        } else if (alvo.hasAttribute("data-voltar-meses")) {
            modo = "dias";
            renderizar();
        } else if (alvo.hasAttribute("data-ano")) {
            mesVisivel = new Date(Number(alvo.dataset.ano), mesVisivel.getMonth(), 1);
            modo = "dias";
            renderizar();
        } else if (alvo.hasAttribute("data-dia")) {
            const dia = String(alvo.dataset.dia).padStart(2, "0");
            const mes = String(mesVisivel.getMonth() + 1).padStart(2, "0");
            campoData.value = `${dia}/${mes}/${mesVisivel.getFullYear()}`;
            campoData.dispatchEvent(new Event("input", { bubbles: true }));
            campoData.dispatchEvent(new Event("change", { bubbles: true }));
            fechar();
        }
    });

    const fechar = () => {
        calendario.hidden = true;
        botaoCalendario.setAttribute("aria-expanded", "false");
    };

    botaoCalendario.addEventListener("click", () => {
        if (calendario.hidden) {
            const dataAtual = lerData(campoData.value);
            mesVisivel = dataAtual ? new Date(dataAtual.getFullYear(), dataAtual.getMonth(), 1) : new Date(anoAtual, hoje.getMonth(), 1);
            modo = "dias";
            renderizar();
            calendario.hidden = false;
            botaoCalendario.setAttribute("aria-expanded", "true");
        } else {
            fechar();
        }
    });

    document.addEventListener("click", (evento) => {
        if (!calendario.contains(evento.target) && !botaoCalendario.contains(evento.target)) fechar();
    });
    document.addEventListener("keydown", (evento) => {
        if (evento.key === "Escape") fechar();
    });
});
