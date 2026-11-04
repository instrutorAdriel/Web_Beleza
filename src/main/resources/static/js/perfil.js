document.addEventListener('DOMContentLoaded', function () {
    const secao = document.getElementById('perfil-agendamento');
    if (!secao) return;

    const botoes = secao.querySelectorAll('.filtro-btn');
    const cards = secao.querySelectorAll('#lista-agendamentos .card-agendamento');
    const msgVazio = document.getElementById('filtro-vazio');

    // Contadores (só contam o que está na tela atual)
    const contadores = {
        TODOS: 0,
        PENDENTE: 0,
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

// Função para exibir o loading
    function mostrarLoading() {
        document.getElementById('loader-global').style.display = 'flex';
    }

    // 1. Aplica o loading APENAS nos links de filtro e paginação de agendamento
    document.addEventListener('DOMContentLoaded', function () {
        // Pega todos os links que tenham 'filtro=' ou 'pagina=' no href (filtros e paginação)
        const linksAgendamento = document.querySelectorAll('a[href*="filtro="], a[href*="pagina="]');

        linksAgendamento.forEach(link => {
            link.addEventListener('click', function () {
                mostrarLoading();
            });
        });
    });

    // 2. Aplica o loading no botão de Cancelar Agendamento (que envia um formulário)
    window.addEventListener('pageshow', function (event) {
        if (event.persisted) {
            document.getElementById('loader-global').style.display = 'none';
        }
    });

    // Remove as mensagens de sucesso ou erro após 4 segundos
    document.addEventListener('DOMContentLoaded', function () {
        const mensagens = document.querySelectorAll('.group-info.success, .group-info.error');
        mensagens.forEach(function (msg) {
            setTimeout(function () {
                // Efeito de fade-out suave
                msg.style.transition = "opacity 0.5s ease";
                msg.style.opacity = "0";
                // Remove do HTML depois que o fade acabar
                setTimeout(() => msg.remove(), 500);
            }, 4000); // 4000 milissegundos = 4 segundos
        });
    });

});