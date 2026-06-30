/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Unidade)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
    inicializarCarrosselDepoimentos();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (SETAS ESQUERDA E DIREITA)
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda:not(.depoimento-seta)');
    const setaDireita = document.querySelector('.seta-direita:not(.depoimento-seta)');

    if (!track || !setaEsquerda || !setaDireita) return;

    setaEsquerda.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: -344, behavior: 'smooth' });
    });

    setaDireita.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: 344, behavior: 'smooth' });
    });
}

/**
 * MODULE: CARROSSEL DE DEPOIMENTOS
 */
function inicializarCarrosselDepoimentos() {
    const track = document.querySelector('.carrossel-depoimentos');
    const setas = document.querySelectorAll('.depoimento-seta');

    if (!track || setas.length === 0) return;

    setas.forEach(seta => {
        seta.addEventListener('click', (e) => {
            e.preventDefault();
            const primeiroCard = track.querySelector('.card-depoimento');
            if (!primeiroCard) return;
            const largura = primeiroCard.offsetWidth + 24;
            track.scrollLeft += seta.classList.contains('seta-esquerda') ? -largura : largura;
        });
    });
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR UNIDADE
 * Remove acentuações e normaliza os termos para garantir o funcionamento do filtro.
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.card-curso-completo');
    const track = document.querySelector('.carrossel-track');

    if (!abas || cardsServicos.length === 0) return;

    // Função auxiliar para remover acentos e caracteres especiais
    const normalizarTexto = (texto) => {
        if (!texto) return '';
        return texto
            .trim()
            .toLowerCase()
            .normalize('NFD') // Divide os caracteres dos seus acentos
            .replace(/[\u0300-\u036f]/g, ''); // Remove os acentos isolados
    };

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            // Normaliza o termo vindo do data-unidade do botão clicado
            const unidadeSelecionada = normalizarTexto(aba.getAttribute('data-unidade'));

            cardsServicos.forEach(card => {
                // Normaliza o termo vindo do data-unidade gerado pelo Thymeleaf
                const unidadeCard = normalizarTexto(card.getAttribute('data-unidade'));

                if (unidadeSelecionada === 'todos' || unidadeCard === unidadeSelecionada) {
                    card.style.setProperty('display', 'flex', 'important');
                } else {
                    card.style.setProperty('display', 'none', 'important');
                }
            });

            if (track) {
                track.scrollLeft = 0;
            }
        });
    });
}

/**
 * MODULE: CAPTURA E SUCESSO DE FORMULÁRIO
 */
function inicializarFormularios() {
    const formularios = document.querySelectorAll('.formulario-matricula form');
    const boxSucesso = document.querySelector('#sucesso .box-sucesso p');

    if (!formularios) return;

    formularios.forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();

            const nomeInput = form.querySelector('input[type="text"]').value;
            const selectUnidade = form.querySelector('select');
            const unidadeSelect = selectUnidade.options[selectUnidade.selectedIndex].text;

            const painelModal = form.closest('.modal-painel');
            const nomeCurso = painelModal ? painelModal.querySelector('h2').textContent : "Serviço Selecionado";

            if (boxSucesso) {
                boxSucesso.innerHTML = `Olá, <strong>${nomeInput}</strong>!<br><br>
                Seu agendamento para o procedimento de <strong>${nomeCurso}</strong> na unidade <strong>${unidadeSelect}</strong> foi processado com sucesso.`;
            }

            window.location.hash = 'sucesso';
            form.reset();

        });

    });

}
/**
 * MODULE: CARROSSEL DE DEPOIMENTOS
 */
function inicializarCarrosselDepoimentos() {
    const track = document.querySelector('.carrossel-depoimentos');
    const setas = document.querySelectorAll('.depoimento-seta');

    if (!track || setas.length === 0) return;

    setas.forEach(seta => {
        seta.addEventListener('click', (e) => {
            e.preventDefault();
            const primeiroCard = track.querySelector('.card-depoimento');
            if (!primeiroCard) return;
            const largura = primeiroCard.offsetWidth + 24;
            track.scrollLeft += seta.classList.contains('seta-esquerda') ? -largura : largura;
        });
    });
}