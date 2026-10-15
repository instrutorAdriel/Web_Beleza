/**
 * Filtro de agendamentos do perfil.
 * - 100% client-side (não faz requisição ao servidor)
 * - Atualiza a URL via history.pushState (mantém ao recarregar)
 * - Conta quantos cards existem por status
 */
document.addEventListener('DOMContentLoaded', function () {

    const secao = document.getElementById('perfil-agendamento');
    if (!secao) return; // não está na aba de agendamento

    const botoes = secao.querySelectorAll('.filtro-btn');
    const cards = secao.querySelectorAll('#lista-agendamentos .card-agendamento');
    const msgVazio = document.getElementById('filtro-vazio');

    // ── 1. Contadores ─────────────────────────────────────────────
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

    // ── 2. Aplica filtro ──────────────────────────────────────────
    function aplicarFiltro(status, atualizarUrl) {
        // botão ativo
        botoes.forEach(b => b.classList.toggle('ativo', b.dataset.status === status));

        // mostra/esconde cards
        let visiveis = 0;
        cards.forEach(card => {
            const mostrar = (status === 'TODOS' || card.dataset.status === status);
            card.style.display = mostrar ? '' : 'none';
            if (mostrar) visiveis++;
        });

        // mensagem de vazio
        msgVazio.style.display = (visiveis === 0) ? 'block' : 'none';

        // URL
        if (atualizarUrl) {
            const url = new URL(window.location.href);
            if (status === 'TODOS') {
                url.searchParams.delete('filtro');
            } else {
                url.searchParams.set('filtro', status);
            }
            window.history.replaceState({}, '', url.toString());
        }
    }

    // ── 3. Cliques ────────────────────────────────────────────────
    botoes.forEach(botao => {
        botao.addEventListener('click', () => {
            aplicarFiltro(botao.dataset.status, true);
        });
    });

    // ── 4. Lê o filtro da URL ao carregar ─────────────────────────
    const params = new URLSearchParams(window.location.search);
    const filtroUrl = params.get('filtro');

    if (filtroUrl) {
        const existe = Array.from(botoes).some(b => b.dataset.status === filtroUrl);
        if (existe) {
            aplicarFiltro(filtroUrl, false);
        } else {
            aplicarFiltro('TODOS', false);
        }
    } else {
        aplicarFiltro('TODOS', false);
    }
});