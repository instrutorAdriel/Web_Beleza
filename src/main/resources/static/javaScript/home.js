document.addEventListener('DOMContentLoaded', () => {
    inicializarHeroSlider();    // Controla o banner principal do topo (com autoplay corrigido)
    inicializarCarrossel();     // Controla as setas dos cards de agendamento
    inicializarFiltroBairros(); // Controla os filtros por região (Taguatinga, Gama, etc)
    inicializarFormularios();   // Controla o envio dos dados e modal de sucesso
});

/**
 * MODULE: BANNER PRINCIPAL (HERO SLIDER DO TOPO)
 * Controla a transição automática e manual dos slides do topo, resetando o tempo no clique.
 */
function inicializarHeroSlider() {
    const slider = document.getElementById('heroSlider');
    const slides = document.querySelectorAll('#heroSlider .slide');
    const btnPrev = document.getElementById('heroBtnPrev');
    const btnNext = document.getElementById('heroBtnNext');

    if (!slider || slides.length === 0) return;

    let index = 0;
    let autoPlayTimer = null;

    function atualizarSlider() {
        slider.style.transform = `translateX(-${index * 100}%)`;
    }

    // Função que inicia ou reinicia o temporizador do zero
    function iniciarAutoPlay() {
        if (autoPlayTimer) clearInterval(autoPlayTimer);

        autoPlayTimer = setInterval(() => {
            index = (index + 1) % slides.length;
            atualizarSlider();
        }, 5000); // Passa sozinho a cada 5 segundos
    }

    if (btnNext) {
        btnNext.addEventListener('click', (e) => {
            e.preventDefault();
            index = (index + 1) % slides.length;
            atualizarSlider();
            iniciarAutoPlay(); // Zera o cronômetro para não pular slide rápido demais
        });
    }

    if (btnPrev) {
        btnPrev.addEventListener('click', (e) => {
            e.preventDefault();
            index = (index - 1 + slides.length) % slides.length;
            atualizarSlider();
            iniciarAutoPlay(); // Zera o cronômetro para não pular slide rápido demais
        });
    }

    // Ativa o autoplay assim que a página carrega
    iniciarAutoPlay();
}

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (SETAS ESQUERDA E DIREITA)
 * Rola o carrossel calculando dinamicamente a largura do card + o espaçamento (gap).
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda');
    const setaDireita = document.querySelector('.seta-direita');

    if (!track) return;

    // Calcula dinamicamente o tamanho do card para a rolagem perfeita
    const getScrollAmount = () => {
        const card = track.querySelector('.card-curso-completo');
        return card ? card.offsetWidth + 24 : 344;
    };

    if (setaEsquerda) {
        setaEsquerda.addEventListener('click', (e) => {
            e.preventDefault();
            track.scrollBy({ left: -getScrollAmount(), behavior: 'smooth' });
        });
    }

    if (setaDireita) {
        setaDireita.addEventListener('click', (e) => {
            e.preventDefault();
            track.scrollBy({ left: getScrollAmount(), behavior: 'smooth' });
        });
    }
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR BAIRRO
 * Limpa o display para ocultar e exibe limpando a propriedade para não quebrar o flexbox.
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.card-curso-completo');
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

                if (bairroSelecionado === 'todos' || cardBairro === bairroSelecionado) {
                    // Remove o display 'none' e volta ao padrão estruturado do CSS/HTML
                    card.style.display = '';
                } else {
                    card.style.display = 'none';
                }
            });

            // Retorna o carrossel para o início ao filtrar
            if (track) {
                track.style.scrollBehavior = 'auto';
                track.scrollLeft = 0;
                track.style.scrollBehavior = 'smooth';
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

            const nomeInput = form.querySelector('input[type="text"]')?.value || 'Usuário';
            const selectUnidade = form.querySelector('select');
            const unidadeSelect = selectUnidade ? selectUnidade.options[selectUnidade.selectedIndex].text : 'Unidade';

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