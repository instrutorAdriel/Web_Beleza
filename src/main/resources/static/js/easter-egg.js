/**
 * SENAC DF - Easter Egg: Cobrinha no Carregamento (V7 - Com Aceleração)
 */

(function () {
    'use strict';

    const TAMANHO_BLOCO = 15;

    // --- INÍCIO DAS NOVAS CONSTANTES (Conforme as imagens) ---
    const VELOCIDADE_MS = 120;      // velocidade inicial
    const VELOCIDADE_MIN_MS = 60;   // limite (mais rápido que isso fica injogável)
    const GANHO_POR_FRUTA_MS = 4;   // quanto acelera a cada fruta
    // ---------------------------------------------------------

    const COR_COBRA = '#f39c12';
    const COR_FRUTA = '#e74c3c';
    const QTD_BOLINHAS_INICIAIS = 4;

    let canvas, ctx;
    let jogoAtivo = false;
    let loopId = null;
    let cobra = [];
    let direcao = { x: 1, y: 0 };
    let proximaDirecao = { x: 1, y: 0 };
    let fruta = { x: 0, y: 0 };
    let pontuacao = 0;

    // --- NOVA VARIÁVEL DE VELOCIDADE ---
    let velocidadeAtual = VELOCIDADE_MS;
    // -----------------------------------

    let carregamentoPendente = null;
    let permitirSaida = false;

    function init() {
        canvas = document.createElement('canvas');
        canvas.id = 'easter-egg-canvas';
        canvas.style.cssText = `
            position: fixed; top: 0; left: 0;
            width: 100vw; height: 100vh;
            z-index: 10001; display: none;
            background-color: transparent; 
        `;
        document.body.appendChild(canvas);
        ctx = canvas.getContext('2d');

        document.addEventListener('keydown', tratarTeclado);

        window.addEventListener('beforeunload', function (e) {
            if (jogoAtivo && !permitirSaida) {
                e.preventDefault();
                e.returnValue = '';
            }
        });
    }

    // --- NOVA FUNÇÃO PARA (RE)INICIAR O LOOP ---
    function iniciarLoop() {
        clearInterval(loopId);
        loopId = setInterval(atualizarJogo, velocidadeAtual);
    }
    // -------------------------------------------

    function tratarTeclado(e) {
        const setas = ['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'];
        const loader = document.getElementById('loader-global');
        const loaderVisivel = loader && window.getComputedStyle(loader).display !== 'none';

        if (!jogoAtivo) {
            if (canvas.dataset.jogoAtivo !== 'true' && loaderVisivel && setas.includes(e.key)) {
                e.preventDefault();
                iniciarJogo(loader);
            }
            return;
        }

        if (e.key === 'Escape' && carregamentoPendente) {
            mostrarConfirmacao();
            return;
        }

        if (setas.includes(e.key)) {
            e.preventDefault();
            if (e.key === 'ArrowUp' && direcao.y === 0) proximaDirecao = { x: 0, y: -1 };
            if (e.key === 'ArrowDown' && direcao.y === 0) proximaDirecao = { x: 0, y: 1 };
            if (e.key === 'ArrowLeft' && direcao.x === 0) proximaDirecao = { x: -1, y: 0 };
            if (e.key === 'ArrowRight' && direcao.x === 0) proximaDirecao = { x: 1, y: 0 };
        }
    }

    function iniciarJogo(loader) {
        jogoAtivo = true;
        permitirSaida = false;
        pontuacao = 0;

        // --- RESETA A VELOCIDADE AO INICIAR O JOGO ---
        velocidadeAtual = VELOCIDADE_MS;
        // ---------------------------------------------

        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight;
        canvas.style.display = 'block';
        canvas.dataset.jogoAtivo = 'true';

        const spinnerOriginal = loader.querySelector('.fa-spinner');
        const textoOriginal = loader.querySelector('p');
        if (spinnerOriginal) spinnerOriginal.style.display = 'none';
        if (textoOriginal) textoOriginal.style.display = 'none';

        loader.style.zIndex = '10000';
        canvas.style.zIndex = '10001';

        const startX = Math.floor(canvas.width / 2 / TAMANHO_BLOCO) * TAMANHO_BLOCO;
        const startY = Math.floor(canvas.height / 2 / TAMANHO_BLOCO) * TAMANHO_BLOCO;

        cobra = [];
        for (let i = 0; i < QTD_BOLINHAS_INICIAIS; i++) {
            cobra.push({ x: startX - (i * TAMANHO_BLOCO), y: startY });
        }

        direcao = { x: 1, y: 0 };
        proximaDirecao = { x: 1, y: 0 };

        gerarFruta();

        // --- TROCA DO SETINTERVAL ANTIGO PELA NOVA FUNÇÃO ---
        iniciarLoop();
        // ----------------------------------------------------
    }

    function gerarFruta() {
        const cols = Math.floor(canvas.width / TAMANHO_BLOCO);
        const rows = Math.floor(canvas.height / TAMANHO_BLOCO);
        fruta = {
            x: Math.floor(Math.random() * cols) * TAMANHO_BLOCO,
            y: Math.floor(Math.random() * rows) * TAMANHO_BLOCO
        };
        if (cobra.some(seg => seg.x === fruta.x && seg.y === fruta.y)) {
            gerarFruta();
        }
    }

    function atualizarJogo() {
        direcao = proximaDirecao;
        const cabeca = {
            x: cobra[0].x + direcao.x * TAMANHO_BLOCO,
            y: cobra[0].y + direcao.y * TAMANHO_BLOCO
        };

        if (cabeca.x < 0) cabeca.x = canvas.width - TAMANHO_BLOCO;
        if (cabeca.x >= canvas.width) cabeca.x = 0;
        if (cabeca.y < 0) cabeca.y = canvas.height - TAMANHO_BLOCO;
        if (cabeca.y >= canvas.height) cabeca.y = 0;

        if (cobra.some(seg => seg.x === cabeca.x && seg.y === cabeca.y)) {
            gameOver();
            return;
        }

        cobra.unshift(cabeca);

        if (cabeca.x === fruta.x && cabeca.y === fruta.y) {
            pontuacao++;
            gerarFruta();

            // --- LÓGICA DE ACELERAÇÃO AO COMER A FRUTA ---
            // Usa Math.max para garantir que a velocidade não caia abaixo do limite mínimo
            const nova = Math.max(VELOCIDADE_MIN_MS, velocidadeAtual - GANHO_POR_FRUTA_MS);

            if (nova !== velocidadeAtual) {
                velocidadeAtual = nova;
                iniciarLoop(); // Reinicia o loop com a nova velocidade
            }
            // ----------------------------------------------

        } else {
            cobra.pop();
        }

        desenhar();
    }

    function desenhar() {
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.textAlign = 'left';

        // Fruta
        ctx.fillStyle = COR_FRUTA;
        ctx.beginPath();
        ctx.arc(fruta.x + TAMANHO_BLOCO / 2, fruta.y + TAMANHO_BLOCO / 2, TAMANHO_BLOCO / 2 - 1, 0, Math.PI * 2);
        ctx.fill();

        ctx.fillStyle = '#27ae60';
        ctx.fillRect(fruta.x + TAMANHO_BLOCO / 2 - 1, fruta.y, 2, 4);

        // Cobra
        cobra.forEach((seg, i) => {
            ctx.fillStyle = COR_COBRA;
            ctx.beginPath();
            const raio = i === 0 ? (TAMANHO_BLOCO / 2) : (TAMANHO_BLOCO / 2 - 2);
            ctx.arc(seg.x + TAMANHO_BLOCO / 2, seg.y + TAMANHO_BLOCO / 2, raio, 0, Math.PI * 2);
            ctx.fill();

            if (i === 0) {
                ctx.fillStyle = '#fff';
                ctx.beginPath();
                ctx.arc(seg.x + TAMANHO_BLOCO / 2 + 2, seg.y + TAMANHO_BLOCO / 2 - 2, 2, 0, Math.PI * 2);
                ctx.fill();
            }
        });

        // Placar
        ctx.fillStyle = '#333';
        ctx.font = 'bold 20px Arial';
        ctx.fillText(`🍎 ${pontuacao}`, 20, 40);

        if (carregamentoPendente) {
            ctx.font = '14px Arial';
            ctx.fillText('✅ Carregou! Aperte ESC para voltar à página', 20, 65);
        }
    }

    function gameOver() {
        clearInterval(loopId);
        jogoAtivo = false;
        permitirSaida = true;

        ctx.clearRect(0, 0, canvas.width, canvas.height);
        ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        ctx.fillStyle = '#333';
        ctx.textAlign = 'center';

        ctx.font = 'bold 40px Arial';
        ctx.fillText('🐍 Fim de Jogo!', canvas.width / 2, canvas.height / 2 - 40);

        ctx.font = '24px Arial';
        ctx.fillText(`Você fez ${pontuacao} ponto(s).`, canvas.width / 2, canvas.height / 2 + 10);

        ctx.font = '18px Arial';
        ctx.fillStyle = '#666';
        ctx.fillText('Clique no botão abaixo para ver os agendamentos.', canvas.width / 2, canvas.height / 2 + 60);

        // Botão de voltar
        ctx.fillStyle = COR_COBRA;
        ctx.fillRect(canvas.width / 2 - 80, canvas.height / 2 + 100, 160, 40);
        ctx.fillStyle = '#fff';
        ctx.font = 'bold 18px Arial';
        ctx.fillText('Continuar', canvas.width / 2, canvas.height / 2 + 126);

        canvas.style.pointerEvents = 'auto';
        canvas.onclick = (e) => {
            const rect = canvas.getBoundingClientRect();
            const clickX = e.clientX - rect.left;
            const clickY = e.clientY - rect.top;

            if (clickX >= canvas.width / 2 - 80 && clickX <= canvas.width / 2 + 80 &&
                clickY >= canvas.height / 2 + 100 && clickY <= canvas.height / 2 + 140) {
                fecharJogo();
            }
        };
    }

    function fecharJogo() {
        clearInterval(loopId);
        loopId = null;
        jogoAtivo = false;

        canvas.style.display = 'none';
        canvas.style.pointerEvents = 'none';
        canvas.dataset.jogoAtivo = 'false';
        canvas.onclick = null;

        const loader = document.getElementById('loader-global');
        if (loader) {
            const spinner = loader.querySelector('.fa-spinner');
            const texto = loader.querySelector('p');
            if (spinner) spinner.style.display = '';
            if (texto) texto.style.display = '';
        }

        if (carregamentoPendente) irParaPagina();
    }

    function mostrarConfirmacao() {
        clearInterval(loopId);
        loopId = null;

        const cx = canvas.width / 2;
        const cy = canvas.height / 2;

        ctx.fillStyle = 'rgba(255, 255, 255, 0.95)';
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        ctx.fillStyle = '#333';
        ctx.textAlign = 'center';
        ctx.font = 'bold 30px Arial';
        ctx.fillText('Carregamento concluído!', cx, cy - 50);
        ctx.font = '18px Arial';
        ctx.fillText('Deseja voltar para a página de agendamentos ou continuar jogando?', cx, cy - 10);
        ctx.fillText(`Sua pontuação atual: ${pontuacao}`, cx, cy + 20);

        ctx.fillStyle = COR_COBRA;
        ctx.fillRect(cx - 160, cy + 70, 140, 40);
        ctx.fillStyle = '#666';
        ctx.fillRect(cx + 20, cy + 70, 140, 40);
        ctx.fillStyle = '#fff';
        ctx.font = 'bold 14px Arial';
        ctx.fillText('Voltar à Página', cx - 90, cy + 95);
        ctx.fillText('Continuar Jogando', cx + 90, cy + 95);

        canvas.style.pointerEvents = 'auto';
        canvas.onclick = function (e) {
            const rect = canvas.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            const naLinha = y >= cy + 70 && y <= cy + 110;

            if (naLinha && x >= cx - 160 && x <= cx - 20) {
                fecharJogo();
            } else if (naLinha && x >= cx + 20 && x <= cx + 160) {
                canvas.style.pointerEvents = 'none';
                canvas.onclick = null;

                // --- TROCA AQUI TAMBÉM PARA MANTER A VELOCIDADE ATUAL ---
                iniciarLoop();
                // --------------------------------------------------------
            }
        };
    }

    document.addEventListener('carregamentoFinalizado', function (evento) {
        carregamentoPendente = evento.detail;

        if (canvas.dataset.jogoAtivo !== 'true') {
            irParaPagina();
            return;
        }

        if (jogoAtivo) mostrarConfirmacao();
    });

    function irParaPagina() {
        const pendente = carregamentoPendente;
        carregamentoPendente = null;
        if (!pendente || !pendente.url) return;

        permitirSaida = true;

        if (pendente.html && window.trocarPagina) {
            window.trocarPagina(pendente.url, pendente.html);
        } else {
            window.location.href = pendente.url;
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();