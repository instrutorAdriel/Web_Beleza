/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Unidade)
 */

document.addEventListener('DOMContentLoaded', () => {
    inicializarHeroBanner(); // NOVO: Inicializa o Hero Banner junto com os outros módulos
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
    inicializarNavAtiva();
    inicializarLogout();
    inicializarTermoConsentimento(); // NOVO: Exige aceite do termo antes de confirmar agendamento
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS (SETAS ESQUERDA E DIREITA)
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda');
    const setaDireita = document.querySelector('.seta-direita');

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
 * MODULE: NAVEGAÇÃO ATIVA POR SCROLL
 * Destaca o link do nav em laranja conforme a seção visível na tela.
 */
function inicializarNavAtiva() {
    const links = document.querySelectorAll('.nav-link');
    const secoes = document.querySelectorAll('section[id]');

    // Clique no menu
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

    // Atualiza menu conforme scroll
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
 * Exibe um popover personalizado perguntando se o usuário tem certeza
 * que deseja sair antes de redirecionar para o logout
 */
function inicializarLogout() { // ← agora está no escopo global
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

    // Inicia o carrossel automático ao carregar
    reiniciarIntervalo();

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






}