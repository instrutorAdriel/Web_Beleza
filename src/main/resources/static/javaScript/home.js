/**
 * SENAC DF - Inteligência Dinâmica do Portal (Serviços e Agendamentos por Unidade)
 */

const ENDPOINT_AVALIACAO = '/api/avaliacoes';

document.addEventListener('DOMContentLoaded', () => {
    inicializarCarrossel();
    inicializarFiltroBairros();
    inicializarFormularios();
    inicializarAvaliacaoModelos();
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
 * MODULE: AVALIAÇÃO DE MODELOS (popup com imagem, descrição e nome do modelo)
 */
function inicializarAvaliacaoModelos() {
    const modal = document.getElementById('modal-avaliacao');
    const form = document.getElementById('form-avaliacao');

    if (!modal || !form) return;

    const inputImagem = document.getElementById('imagemResultado');
    const previewWrapper = document.getElementById('previewImagem');
    const feedback = document.getElementById('feedbackAvaliacao');
    const inputNomeModelo = document.getElementById('nomeModelo');
    const btnEnviar = document.getElementById('btn-enviar-avaliacao');

    // --- Abertura do modal a partir de qualquer botão com data-abrir-avaliacao ---
    document.querySelectorAll('[data-abrir-avaliacao]').forEach((btn) => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const nomeModelo = btn.getAttribute('data-abrir-avaliacao');
            if (nomeModelo) {
                inputNomeModelo.value = nomeModelo;
            }
            abrirModalAvaliacao();
        });
    });

    function abrirModalAvaliacao() {
        modal.classList.add('ativo');
        modal.style.display = 'flex';
    }

    function fecharModalAvaliacao() {
        modal.classList.remove('ativo');
        modal.style.display = 'none';
        form.reset();
        previewWrapper.innerHTML = '';
        previewWrapper.classList.remove('ativo');
        feedback.textContent = '';
        feedback.className = 'feedback-avaliacao';
    }

    document.getElementById('btn-fechar-avaliacao').addEventListener('click', (e) => {
        e.preventDefault();
        fecharModalAvaliacao();
    });

    document.getElementById('btn-cancelar-avaliacao').addEventListener('click', (e) => {
        e.preventDefault();
        fecharModalAvaliacao();
    });

    // Fecha clicando fora do painel
    modal.addEventListener('click', (e) => {
        if (e.target === modal) fecharModalAvaliacao();
    });

    // --- Preview da imagem selecionada ---
    inputImagem.addEventListener('change', () => {
        const arquivo = inputImagem.files[0];
        previewWrapper.innerHTML = '';

        if (!arquivo) {
            previewWrapper.classList.remove('ativo');
            return;
        }

        const leitor = new FileReader();
        leitor.onload = (e) => {
            const img = document.createElement('img');
            img.src = e.target.result;
            previewWrapper.appendChild(img);
            previewWrapper.classList.add('ativo');
        };
        leitor.readAsDataURL(arquivo);
    });

    // --- Envio do formulário para o backend Spring ---
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        feedback.textContent = '';
        feedback.className = 'feedback-avaliacao';

        const descricao = document.getElementById('descricaoAvaliacao').value.trim();
        const arquivo = inputImagem.files[0];

        if (!arquivo || !descricao) {
            feedback.textContent = 'Preencha a imagem e a descrição da avaliação.';
            feedback.classList.add('erro');
            return;
        }

        const formData = new FormData();
        formData.append('imagem', arquivo);
        formData.append('descricao', descricao);
        formData.append('nomeModelo', inputNomeModelo.value.trim()); // pode ir vazio

        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Enviando...';

        try {
            const resposta = await fetch(ENDPOINT_AVALIACAO, {
                method: 'POST',
                body: formData
                // Não defina Content-Type manualmente: o navegador
                // monta o boundary correto do multipart/form-data.
            });

            if (!resposta.ok) {
                throw new Error('Falha ao enviar avaliação (status ' + resposta.status + ')');
            }

            feedback.textContent = 'Avaliação enviada com sucesso!';
            feedback.classList.add('sucesso');

            setTimeout(fecharModalAvaliacao, 1200);

        } catch (erro) {
            console.error(erro);
            feedback.textContent = 'Erro ao enviar a avaliação. Tente novamente.';
            feedback.classList.add('erro');
        } finally {
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar Avaliação';
        }
    });
}