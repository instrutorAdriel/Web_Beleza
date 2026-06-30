/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Unidade)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarHeroBanner();
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
});

/**
 * MODULE: CONTROLE DO HERO BANNER (SLIDESHOW AUTOMÁTICO E MANUAL)
 * Ajustado exatamente para a estrutura do seu HTML (id="heroSlider", class="slide", etc.)
 */
function inicializarHeroBanner() {
    const sliderContainer = document.querySelector('#heroSlider');
    const slides = document.querySelectorAll('#heroSlider .slide');
    const setaEsquerda = document.querySelector('#heroBtnPrev');
    const setaDireita = document.querySelector('#heroBtnNext');

    if (slides.length === 0) return;

    let slideAtual = 0;
    let intervaloBanner;
    const tempoTransicao = 5000; // Alterna automaticamente a cada 5 segundos

    // Configuração inicial de estilo dos slides caso não estejam mapeados no CSS
    if (sliderContainer) {
        sliderContainer.style.position = 'relative';
        sliderContainer.style.overflow = 'hidden';
    }

    slides.forEach((slide, idx) => {
        slide.style.position = 'absolute';
        slide.style.top = '0';
        slide.style.left = '0';
        slide.style.width = '100%';
        slide.style.height = '100%';
        slide.style.transition = 'opacity 0.8s ease-in-out';

        // Deixa apenas o primeiro slide visível inicialmente
        if (idx === 0) {
            slide.classList.add('ativa');
            slide.style.opacity = '1';
            slide.style.zIndex = '2';
        } else {
            slide.classList.remove('ativa');
            slide.style.opacity = '0';
            slide.style.zIndex = '1';
        }
    });

    // Função para transição e exibição dos slides
    function mostrarSlide(index) {
        // Trata os limites (loop infinito)
        if (index >= slides.length) slideAtual = 0;
        else if (index < 0) slideAtual = slides.length - 1;
        else slideAtual = index;

        // Atualiza a opacidade de cada slide baseado no índice ativo
        slides.forEach((slide, idx) => {
            if (idx === slideAtual) {
                slide.classList.add('ativa');
                slide.style.opacity = '1';
                slide.style.zIndex = '2';
            } else {
                slide.classList.remove('ativa');
                slide.style.opacity = '0';
                slide.style.zIndex = '1';
            }
        });
    }

    function proximoSlide() {
        mostrarSlide(slideAtual + 1);
    }

    function slideAnterior() {
        mostrarSlide(slideAtual - 1);
    }

    function reiniciarIntervalo() {
        clearInterval(intervaloBanner);
        intervaloBanner = setInterval(proximoSlide, tempoTransicao);
    }

    // Ouvintes de evento para as setas do seu HTML
    if (setaDireita) {
        setaDireita.addEventListener('click', (e) => {
            e.preventDefault();
            proximoSlide();
            reiniciarIntervalo();
        });
    }

    if (setaEsquerda) {
        setaEsquerda.addEventListener('click', (e) => {
            e.preventDefault();
            slideAnterior();
            reiniciarIntervalo();
        });
    }

    // Inicia o temporizador automático do banner
    intervaloBanner = setInterval(proximoSlide, tempoTransicao);
}

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (SETAS ESQUERDA E DIREITA)
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-carrossel.seta-esquerda'); // Proteção extra se houver mais setas na página
    const setaDireita = document.querySelector('.seta-carrossel.seta-direita');

    if (!track || !setaDireita) return; // Note que seu HTML só possui a seta-direita declarada explicitamente, garantimos que não quebre.

    if (setaEsquerda) {
        setaEsquerda.addEventListener('click', (e) => {
            e.preventDefault();
            track.scrollBy({ left: -344, behavior: 'smooth' });
        });
    }

    setaDireita.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: 344, behavior: 'smooth' });
    });
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR BAIRRO
 * Atualizado de "data-unidade" para "data-bairro" para casar perfeitamente com o seu HTML.
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.card-curso-completo');
    const track = document.querySelector('.carrossel-track');

    if (abas.length === 0 || cardsServicos.length === 0) return;

    // Função auxiliar para remover acentos e caracteres especiais
    const normalizarTexto = (texto) => {
        if (!texto) return '';
        return texto
            .trim()
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '');
    };

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            // Gerencia a classe visual nos botões de filtro
            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            // Captura o bairro selecionado usando data-bairro do seu HTML
            const bairroSelecionado = normalizarTexto(aba.getAttribute('data-bairro'));

            cardsServicos.forEach(card => {
                // Captura o bairro do card correspondente
                const bairroCard = normalizarTexto(card.getAttribute('data-bairro'));

                if (bairroSelecionado === 'todos' || bairroCard === bairroSelecionado) {
                    card.style.setProperty('display', 'flex', 'important');
                } else {
                    card.style.setProperty('display', 'none', 'important');
                }
            });

            // Reseta o carrossel de volta para o início ao filtrar
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

    if (formularios.length === 0) return;

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