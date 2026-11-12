/**
 * SENAC DF - Lógica e Interações da Página de Perfil
 */

document.addEventListener('DOMContentLoaded', function () {
    inicializarFiltrosAgendamento();
    inicializarNotificacoes();
    inicializarCarregamentoAgendamento();
    inicializarMascaraTelefone();
    interceptarCliquesParaEasterEgg();
});

function trocarPagina(url, html) {
    history.pushState({ eeSwap: true }, '', url);
    document.open();
    document.write(html);
    document.close();
}
window.trocarPagina = trocarPagina;

// Botão "voltar" do navegador depois de uma troca
window.addEventListener('popstate', function () {
    location.reload();
});

/**
 * Filtros de status dos Agendamentos no Perfil
 */
function inicializarFiltrosAgendamento() {
    const secao = document.getElementById('perfil-agendamento');
    if (!secao) return;

    const botoes = secao.querySelectorAll('.filtro-btn');
    const cards = secao.querySelectorAll('#lista-agendamentos .card-agendamento');
    const msgVazio = document.getElementById('filtro-vazio');

    // Contadores locais para os agendamentos visíveis
    const contadores = {
        TODOS: 0,
        CONFIRMADO: 0,
        REALIZADO: 0,
        CANCELADO: 0,
        NAO_COMPARECEU: 0
    };

    cards.forEach(card => {
        contadores.TODOS++;
        const status = card.dataset.status;
        if (Object.prototype.hasOwnProperty.call(contadores, status)) {
            contadores[status]++;
        }
    });

    Object.keys(contadores).forEach(status => {
        const el = secao.querySelector(`.filtro-contador[data-contador="${status}"]`);
        if (el) el.textContent = contadores[status];
    });

    function aplicarFiltro(status) {
        botoes.forEach(b => b.classList.toggle('ativo', b.dataset.status === status));

        let visiveis = 0;
        cards.forEach(card => {
            const mostrar = (status === 'TODOS' || card.dataset.status === status);
            card.style.display = mostrar ? '' : 'none';
            if (mostrar) visiveis++;
        });

        if (msgVazio) {
            msgVazio.style.display = (visiveis === 0) ? 'block' : 'none';
        }
    }

    botoes.forEach(botao => {
        botao.addEventListener('click', () => {
            aplicarFiltro(botao.dataset.status);
        });
    });
}

/**
 * Exibe o spinner global de carregamento
 */
function mostrarLoading() {
    const loader = document.getElementById('loader-global');
    if (loader) loader.style.display = 'flex';
}

/**
 * Eventos de carregamento para a aba de agendamentos
 */
function inicializarCarregamentoAgendamento() {
    const linksAgendamento = document.querySelectorAll('a[href*="filtro="], a[href*="pagina="]');
    linksAgendamento.forEach(link => {
        link.addEventListener('click', function () {
            mostrarLoading();
        });
    });

    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            const loader = document.getElementById('loader-global');
            if (loader) loader.style.display = 'none';
        }
    });
}

/**
 * Efeito de fade-out nas mensagens de sucesso ou erro do perfil
 */
function inicializarNotificacoes() {
    const mensagens = document.querySelectorAll('.group-info.success, .group-info.error');
    mensagens.forEach(function (msg) {
        setTimeout(function () {
            msg.style.transition = "opacity 0.5s ease";
            msg.style.opacity = "0";
            setTimeout(() => msg.remove(), 500);
        }, 4000);
    });
}


function inicializarMascaraTelefone() {
    const inputTelefone = document.getElementById('telefone');
    if (!inputTelefone) return;

    function aplicarMascaraTelefone(valor) {
        valor = valor.replace(/\D/g, '');
        valor = valor.substring(0, 11);

        if (valor.length > 10) {
            valor = valor.replace(/^(\d{2})(\d{5})(\d{4})$/, '($1) $2-$3');
        } else if (valor.length > 6) {
            valor = valor.replace(/^(\d{2})(\d{4})(\d{0,4})$/, '($1) $2-$3');
        } else if (valor.length > 2) {
            valor = valor.replace(/^(\d{2})(\d{0,4})$/, '($1) $2');
        } else if (valor.length > 0) {
            valor = valor.replace(/^(\d{0,2})$/, '($1');
        }

        return valor;
    }

    inputTelefone.addEventListener('input', function () {
        this.value = aplicarMascaraTelefone(this.value);
    });
}

/**
 * Interceptador de cliques para o Easter Egg
 * Busca os agendamentos via AJAX para não recarregar a página e não matar o jogo.
 */
function interceptarCliquesParaEasterEgg() {
    document.addEventListener('click', async function (e) {
        if (e.button !== 0 || e.ctrlKey || e.metaKey || e.shiftKey || e.altKey) return;

        const link = e.target.closest('.filtro-btn, .btn-paginacao-aba');
        if (!link) return;
        if (!document.getElementById('easter-egg-canvas')) return;

        const url = link.getAttribute('href');
        if (!url || url.startsWith('#')) return;

        e.preventDefault();
        e.stopImmediatePropagation();
        mostrarLoading();

        try {
            const resp = await fetch(url, { credentials: 'same-origin' });
            const tipo = resp.headers.get('content-type') || '';
            if (!resp.ok || !tipo.includes('text/html')) throw new Error('Resposta inválida');

            const html = await resp.text();
            document.dispatchEvent(new CustomEvent('carregamentoFinalizado', {
                detail: { url: resp.url || url, html }
            }));
        } catch (erro) {
            console.error('Erro ao carregar agendamentos:', erro);
            window.location.href = url; // fallback: navegação normal
        }
    }, true);
}

