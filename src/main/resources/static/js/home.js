/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Unidade)
 */

let posicaoScrollAtual = 0;

document.addEventListener('DOMContentLoaded', () => {
    inicializarHeroBanner();
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
    inicializarCarrosselDepoimentos();
    inicializarNavAtiva();
    inicializarLogout();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (EXPANSÃO INTELIGENTE E TRAVA DE TAMANHO)
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track-servicos');
    const setaEsquerda = document.querySelector('.seta-servico.seta-esquerda');
    const setaDireita = document.querySelector('.seta-servico.seta-direita');
    const viewport = document.querySelector('.carrossel-viewport-servicos');
    const cards = document.querySelectorAll('.carrossel-track-servicos .card-curso-completo');

    if (!track || !setaEsquerda || !setaDireita) return;

    setaEsquerda.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: -344, behavior: 'smooth' });
    });

    setaDireita.addEventListener('click', (e) => {
        e.preventDefault();
        track.scrollBy({ left: 344, behavior: 'smooth' });
    });

    if (viewport && cards.length > 0) {
        cards.forEach(card => {
            card.addEventListener('mouseenter', () => {
                viewport.style.height = viewport.offsetHeight + 'px';
            });
            card.addEventListener('mouseleave', () => {
                viewport.style.height = '';
            });
        });
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
 * MODULE: FILTRAGEM DINÂMICA DE SERVIÇOS POR UNIDADE (COM MENSAGEM DE VAZIO)
 */
function inicializarFiltroBairros() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsServicos = document.querySelectorAll('.card-servico, .card-curso-completo');
    const track = document.querySelector('.carrossel-track-servicos, .carrossel-track');
    const mensagemVazia = document.querySelector('#mensagem-vazia');

    if (!abas || cardsServicos.length === 0) return;

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

            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            const unidadeSelecionada = normalizarTexto(aba.getAttribute('data-unidade'));
            let contadorVisiveis = 0;

            cardsServicos.forEach(card => {
                const unidadeCard = normalizarTexto(card.getAttribute('data-unidade'));

                if (unidadeSelecionada === 'todos' || unidadeCard === unidadeSelecionada) {
                    card.style.setProperty('display', 'flex', 'important');
                    contadorVisiveis++;
                } else {
                    card.style.setProperty('display', 'none', 'important');
                }
            });

            if (contadorVisiveis === 0) {
                if (mensagemVazia) mensagemVazia.style.display = 'block';
                if (track) track.style.display = 'none';
            } else {
                if (mensagemVazia) mensagemVazia.style.display = 'none';
                if (track) track.style.display = 'flex';
            }

            if (track) {
                track.scrollLeft = 0;
            }
        });
    });

    const abaAtiva = document.querySelector('.aba-filtro.ativa');
    if (abaAtiva) abaAtiva.click();
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
 * MODULE: NAVEGAÇÃO ATIVA POR SCROLL
 */
function inicializarNavAtiva() {
    const links = document.querySelectorAll('.nav-link');
    const secoes = document.querySelectorAll('section[id]');

    links.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const destino = document.querySelector(this.getAttribute('href'));
            if (destino) {
                destino.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });

    window.addEventListener('scroll', () => {
        let secaoAtual = '';

        secoes.forEach(secao => {
            const topo = secao.offsetTop - 120;
            const altura = secao.offsetHeight;

            if (window.scrollY >= topo && window.scrollY < topo + altura) {
                secaoAtual = secao.getAttribute('id');
            }
        });

        links.forEach(link => {
            link.classList.remove('ativo');
            if (link.getAttribute('href') === `#${secaoAtual}`) {
                link.classList.add('ativo');
            }
        });
    });
}

/**
 * MODULE: CONFIRMAÇÃO DE LOGOUT
 */
function inicializarLogout() {
    const btnSair = document.querySelector('.btn-sair');

    if (!btnSair) return;

    btnSair.addEventListener('click', function (e) {
        e.preventDefault();

        const popover = document.createElement('div');
        popover.className = 'popover-logout';
        popover.innerHTML = `
            <p>Tem certeza que deseja sair?</p>
            <div class="popover-botoes">
                <button class="popover-btn-sim">Sim, sair</button>
                <button class="popover-btn-nao">Cancelar</button>
            </div>
        `;

        document.querySelector('.popover-logout')?.remove();
        document.body.appendChild(popover);

        popover.querySelector('.popover-btn-sim').addEventListener('click', () => {
            window.location.href = '/logout';
        });

        popover.querySelector('.popover-btn-nao').addEventListener('click', () => {
            popover.remove();
        });

        setTimeout(() => {
            document.addEventListener('click', function fechar(e) {
                if (!popover.contains(e.target) && e.target !== btnSair) {
                    popover.remove();
                    document.removeEventListener('click', fechar);
                }
            });
        }, 100);
    });
}

/**
 * MODULE: CONTROLE DO HERO BANNER (SLIDESHOW AUTOMÁTICO E MANUAL)
 */
function inicializarHeroBanner() {
    const sliderContainer = document.querySelector('#heroSlider');
    const slides = document.querySelectorAll('#heroSlider .slide');
    const setaEsquerda = document.querySelector('#heroBtnPrev');
    const setaDireita = document.querySelector('#heroBtnNext');

    if (slides.length === 0) return;

    let slideAtual = 0;
    let intervaloBanner;
    const tempoTransicao = 5000;

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

    function mostrarSlide(index) {
        if (index >= slides.length) slideAtual = 0;
        else if (index < 0) slideAtual = slides.length - 1;
        else slideAtual = index;

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

    reiniciarIntervalo();

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
}

/**
 * MODULE: FILTROS DE DEPOIMENTOS
 */
function aplicarFiltros() {
    const unidadeEl = document.getElementById('filtro-unidade');
    const servicoEl = document.getElementById('filtro-servico');

    if (!unidadeEl || !servicoEl) return;

    const unidade = unidadeEl.value;
    const servico = servicoEl.value;

    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('unidade', unidade);
    urlParams.set('servico', servico);
    urlParams.set('pagina', 0);

    window.location.href = window.location.pathname + '?' + urlParams.toString() + '#depoimentos';
}

/**
 * MODULE: MODAL DE DEPOIMENTO EXPANDIDO
 */
function abrirModalDepoimento(card) {
    posicaoScrollAtual = window.scrollY;

    const nome = card.getAttribute('data-nome');
    const texto = card.getAttribute('data-texto');
    const img1 = card.getAttribute('data-img1');
    const img2 = card.getAttribute('data-img2');
    const servico = card.getAttribute('data-servico');
    const avatar = card.getAttribute('data-avatar');

    document.getElementById('modal-dep-nome').innerText = nome;
    document.getElementById('modal-dep-texto').innerText = '"' + texto + '"';
    document.getElementById('modal-dep-servico').innerText = servico;
    document.getElementById('modal-dep-avatar').innerText = avatar;

    const imgElement1 = document.getElementById('modal-dep-img1');
    const imgElement2 = document.getElementById('modal-dep-img2');

    if (img1 && img1 !== 'null' && img1 !== '') {
        imgElement1.src = img1;
        imgElement1.style.display = 'block';
    } else {
        imgElement1.style.display = 'none';
    }

    if (img2 && img2 !== 'null' && img2 !== '') {
        imgElement2.src = img2;
        imgElement2.style.display = 'block';
    } else {
        imgElement2.style.display = 'none';
    }

    imgElement1.parentElement.classList.toggle(
        'imagem-unica',
        [imgElement1, imgElement2].filter(img => img.style.display !== 'none').length === 1
    );

    window.location.hash = '#modal-depoimento';
}

function fecharModalDepoimento(event) {
    if (event) event.preventDefault();
    window.location.hash = '';

    window.scrollTo({
        top: posicaoScrollAtual,
        behavior: 'instant'
    });
}