/**
 * SENAC DF - Inteligência Dinâmica do Portal (Cursos e Agendamentos)
 */

document.addEventListener('DOMContentLoaded', () => {
    // Inicializa todos os módulos da página
    inicializarCarrossel();
    inicializarFiltroCursos();
    inicializarFormularios();
    inicializarNavAtiva();
    inicializarLogout();
});

/**
 * MODULE: CONTROLE DO CARROSSEL DE ATENDIMENTOS
 * Faz as setas arrastarem os cards de forma inteligente baseada no tamanho da tela
 */
function inicializarCarrossel() {
    const track = document.querySelector('.carrossel-track');
    const setaEsquerda = document.querySelector('.seta-esquerda');
    const setaDireita = document.querySelector('.seta-direita');

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

        // Descobre a largura exata do card + o espaçamento (gap = 24px)
        const larguraCard = primeiroCard.offsetWidth + 24;

        // Executa a rolagem
        track.scrollLeft += (direcao * larguraCard);
    }
}

/**
 * MODULE: FILTRAGEM DINÂMICA DE CURSOS
 * Filtra os cards buscando a classe correspondente (TI, GASTRO, SAUDE) com base no texto da aba
 */
function inicializarFiltroCursos() {
    const abas = document.querySelectorAll('.aba-filtro');
    const cardsCursos = document.querySelectorAll('.card-curso-completo');

    abas.forEach(aba => {
        aba.addEventListener('click', (e) => {
            e.preventDefault();

            // Atualiza o estado visual das abas
            abas.forEach(a => a.classList.remove('ativa'));
            aba.classList.add('ativa');

            // Normaliza o texto da aba para mapear as classes (ex: "Tecnologia" -> "TI")
            const textoAba = aba.textContent.trim().toUpperCase();
            let classeFiltro = '';

            if (textoAba === 'TECNOLOGIA') classeFiltro = 'TI';
            else if (textoAba === 'GASTRONOMIA') classeFiltro = 'GASTRO';
            else if (textoAba === 'SAÚDE' || textoAba === 'SAUDE') classeFiltro = 'SAUDE';

            // Filtra os cards na tela
            cardsCursos.forEach(card => {
                const placeholder = card.querySelector('.img-placeholder-curso');

                // Se for 'Todos' ou se o card possuir a classe da categoria correspondente
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
 * Captura os dados digitados na matrícula e personaliza o modal de sucesso correto (#sucesso)
 */
function inicializarFormularios() {
    const formularios = document.querySelectorAll('.formulario-matricula form');
    const boxSucesso = document.querySelector('#sucesso .box-sucesso p');

    formularios.forEach(form => {
        form.addEventListener('submit', (e) => {
            e.preventDefault();

            // Coleta os dados que o usuário digitou
            const nomeInput = form.querySelector('input[type="text"]').value;
            const emailInput = form.querySelector('input[type="email"]').value;
            const selectUnidade = form.querySelector('select');
            const unidadeSelect = selectUnidade.options[selectUnidade.selectedIndex].text;

            // Busca o título do curso que está no topo do modal corrente
            const painelModal = form.closest('.modal-painel');
            const nomeCurso = painelModal ? painelModal.querySelector('h2').textContent : "Curso Selecionado";

            // Altera dinamicamente o texto da tela de confirmação do ID #sucesso
            if (boxSucesso) {
                boxSucesso.innerHTML = `Olá, <strong>${nomeInput}</strong>!<br><br>
                Sua pré-inscrição para o curso de <strong>${nomeCurso}</strong> na <strong>${unidadeSelect}</strong> foi processada com sucesso.<br><br>
                Enviamos um e-mail de confirmação para <strong>${emailInput}</strong> contendo a lista de documentos necessários para a efetivação da sua vaga.`;
            }

            // Redireciona o alvo do CSS :target para exibir o modal correto de sucesso
            window.location.hash = 'sucesso';

            // Limpa os campos do formulário
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
            window.location.href = '/usuario/logout';
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