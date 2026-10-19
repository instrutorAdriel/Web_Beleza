document.addEventListener('DOMContentLoaded', function () {
    const secao = document.getElementById('perfil-agendamento');
    if (!secao) return;

    const botoes = secao.querySelectorAll('.filtro-btn');
    const cards = secao.querySelectorAll('#lista-agendamentos .card-agendamento');
    const msgVazio = document.getElementById('filtro-vazio');

    // Contadores (só contam o que está na tela atual)
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

        msgVazio.style.display = (visiveis === 0) ? 'block' : 'none';
    }

    botoes.forEach(botao => {
        botao.addEventListener('click', () => {
            aplicarFiltro(botao.dataset.status);
        });
    });
});