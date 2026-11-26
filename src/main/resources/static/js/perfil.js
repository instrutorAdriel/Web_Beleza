/**
 * SENAC DF - Lógica e Interações da Página de Perfil
 */

document.addEventListener('DOMContentLoaded', function () {
    inicializarNotificacoes();
    inicializarCarregamentoAgendamento();
    inicializarMascaraTelefone();
    inicializarContadorDepoimento();
});

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
            msg.style.transition = 'opacity 0.5s ease';
            msg.style.opacity = '0';
            setTimeout(() => msg.remove(), 500);
        }, 4000);
    });
}

/**
 * Máscara de telefone (XX) XXXXX-XXXX (15 caracteres)
 */
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
 * Contador de caracteres do campo de depoimento (limite 255)
 */
function inicializarContadorDepoimento() {
    const campoDepoimento = document.getElementById('campoTextoDepoimento');
    const contadorDepoimento = document.getElementById('contadorCaracteresDepoimento');
    const limite = 255;

    if (!campoDepoimento || !contadorDepoimento) return;

    function atualizarContador() {
        const quantidade = campoDepoimento.value.length;
        contadorDepoimento.textContent = quantidade + '/' + limite;
        contadorDepoimento.style.color = quantidade >= limite ? '#dc2626' : '#6b7280';
    }

    campoDepoimento.addEventListener('input', atualizarContador);
    atualizarContador();
}