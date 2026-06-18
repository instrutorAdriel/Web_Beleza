/**
 * SENAC DF - Inteligência Dinâmica do Portal (Cursos e Agendamentos)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarCarrossel();
    inicializarFiltroCursos();
    inicializarFormularios();
    inicializarCarrosselDepoimentos();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda:not(.depoimento-seta)');
    const setaDireita = document.querySelector('.seta-direita:not(.depoimento-seta)');

    if (!track || !setaEsquerda || !setaDireita) return;

    setaEsquerda.addEventListener('click', (e) => {
        e.preventDefault();
        moverCarrossel(-1);
    });

    setaDireita.addEventListener('click', (e) => {
        e.preventDefault();
        moverCarrossel(1);
    });

    function moverCarrossel(direcao) {
        const primeiroCard = track.querySelector('.card-servico');
        if (!primeiroCard) return;
        const larguraCard = primeiroCard.offsetWidth + 24;
        track.scrollLeft += (direcao * larguraCard);
    }
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
 * MODULE: FILTRAGEM DINÂMICA DE CURSOS
 */
function inicializarFiltroCursos() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsCursos = document.querySelectorAll('.card-curso-completo');

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            const textoAba = aba.textContent.trim().toUpperCase();
            let classeFiltro = '';

            if (textoAba === 'TECNOLOGIA') classeFiltro = 'TI';
            else if (textoAba === 'GASTRONOMIA') classeFiltro = 'GASTRO';
            else if (textoAba === 'SAÚDE' || textoAba === 'SAUDE') classeFiltro = 'SAUDE';

            cardsCursos.forEach(card => {
                const placeholder = card.querySelector('.img-placeholder-curso');
                if (textoAba === 'TODOS' || (placeholder && placeholder.classList.contains(classeFiltro))) {
                    card.style.display = 'flex';
                } else {
                    card.style.display = 'none';
                }
            });
        });
    });
}

/**
 * MODULE: CAPTURA E SUCESSO DE FORMULÁRIO
 */
function inicializarFormularios() {
    const formularios = document.querySelectorAll('.formulario-matricula form');
    const boxSucesso = document.querySelector('#sucesso .box-sucesso p');

    formularios.forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();

            const nomeInput = form.querySelector('input[type="text"]').value;
            const emailInput = form.querySelector('input[type="email"]').value;
            const selectUnidade = form.querySelector('select');
            const unidadeSelect = selectUnidade.options[selectUnidade.selectedIndex].text;

            const painelModal = form.closest('.modal-painel');
            const nomeCurso = painelModal ? painelModal.querySelector('h2').textContent : "Curso Selecionado";

            if (boxSucesso) {
                boxSucesso.innerHTML = `Olá, <strong>${nomeInput}</strong>!<br><br>
                Sua pré-inscrição para o curso de <strong>${nomeCurso}</strong> na <strong>${unidadeSelect}</strong> foi processada com sucesso.<br><br>
                Enviamos um e-mail de confirmação para <strong>${emailInput}</strong> contendo a lista de documentos necessários para a efetivação da sua vaga.`;
            }

            window.location.hash = 'sucesso';
            form.reset();
        });
    });
}