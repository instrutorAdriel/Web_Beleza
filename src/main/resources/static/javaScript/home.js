/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Bairro)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
    inicializarValidacaoIdade();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (SETAS ESQUERDA E DIREITA)
 * Rola o carrossel de forma dinâmica baseando-se nos cards que estão visíveis.
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda');
    const setaDireita = document.querySelector('.seta-direita');

    if (!track || !setaEsquerda || !setaDireita) return;

    // Seta para a Esquerda
    setaEsquerda.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: -344, behavior: 'smooth' });
    });

    // Seta para a Direita
    setaDireita.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: 344, behavior: 'smooth' });
    });
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR BAIRRO
 * Corrige o display para manter o comportamento de flexbox do carrossel.
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.carrossel-track .card-curso-completo');
    const track = document.querySelector('.carrossel-track');

    if (!abas || !cardsServicos) return;

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            // Gerencia a classe visual nos botões de filtro
            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            const bairroSelecionado = aba.getAttribute('data-bairro');

            cardsServicos.forEach(card => {
                const cardBairro = card.getAttribute('data-bairro');

                // CORREÇÃO: Mudado de 'block' para 'flex' para não quebrar a estrutura do carrossel horizontal
                if (bairroSelecionado === 'todos' || cardBairro === bairroSelecionado) {
                    card.style.setProperty('display', 'flex', 'important');
                } else {
                    card.style.setProperty('display', 'none', 'important');
                }
            });

            // RETORNA O CARROSSEL PARA O INÍCIO (Garante que os botões funcionem a partir do 1º card do filtro)
            if (track) {
                track.scrollLeft = 0;
            }
        });
    });
}

/**
 * MODULE: VALIDAÇÃO DE IDADE PARA AGENDAMENTO (+16 ANOS)
 */
function inicializarValidacaoIdade() {
    const botoesAgendar = document.querySelectorAll('.btn-detalhes');

    botoesAgendar.forEach(botao => {
        botao.addEventListener('click', (e) => {
            const confirmouIdade = confirm("Atenção: Para realizar o agendamento e receber o atendimento, você deve ter 16 anos ou mais. Você confirma que cumpre este requisito?");

            if (!confirmouIdade) {
                e.preventDefault();
                console.log("Agendamento bloqueado: Idade insuficiente.");
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