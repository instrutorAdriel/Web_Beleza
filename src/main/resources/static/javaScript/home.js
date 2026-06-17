/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Bairro)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS
 * Gerencia a rolagem horizontal matemática exata com tratamento de clique
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda');
    const setaDireita = document.querySelector('.seta-direita');

    if (!track || !setaEsquerda || !setaDireita) return;

    setaEsquerda.addEventListener('click', () => {
        // Rola para a esquerda baseado no tamanho atual de um card + gap
        track.scrollBy({ left: -344, behavior: 'smooth' });
    });

    setaDireita.addEventListener('click', () => {
        // Rola para a direita baseado no tamanho atual de um card + gap
        track.scrollBy({ left: 344, behavior: 'smooth' });
    });
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR BAIRRO
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.carrossel-track .card-curso-completo');
    const track = document.querySelector('.carrossel-track');

    if (!abas || !cardsServicos) return;

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            const bairroSelecionado = aba.getAttribute('data-bairro');

            cardsServicos.forEach(card => {
                const cardBairro = card.getAttribute('data-bairro');

                // Garante que o layout flex permaneça intacto ao reexibir os cards filtrados
                if (bairroSelecionado === 'todos' || cardBairro === bairroSelecionado) {
                    card.style.display = 'block';
                } else {
                    card.style.display = 'none';
                }
            });

            // Reposiciona o carrossel no início ao alternar os filtros
            if (track) track.scrollLeft = 0;
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
            const emailInput = form.querySelector('input[type="email"]').value;
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