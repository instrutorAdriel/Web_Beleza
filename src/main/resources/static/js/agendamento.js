let requisicaoEmAndamento = false;

// Abre o modal e pede ao Spring o HTML pronto (Fragmento) da tabela
function abrirModalAgendamento(idServico) {
    console.log("Carregando tabela via Spring para o Serviço:", idServico);

    document.getElementById("modal-agendamento").dataset.idServicoAtual = idServico;
    document.getElementById("modal-agendamento").style.display = "flex";

    // NOVO: mostra "Carregando..." enquanto a requisição acontece
    document.getElementById("tabela-sessoes-body").innerHTML =
        '<tr><td colspan="4" style="text-align:center;padding:24px;color:#999;">' +
        '<i class="fa-solid fa-spinner fa-spin"></i> Carregando horários...</td></tr>';

    fetch(`/api/agendamento/modal-tabela?servicoId=${idServico}`)
        .then(response => {
            if (!response.ok) throw new Error("Erro na requisição da tabela");
            return response.text();
        })
        .then(htmlDoFragmento => {
            document.getElementById("tabela-sessoes-body").innerHTML = htmlDoFragmento;
        })
        .catch(error => {
            console.error("Erro ao carregar fragmento:", error);
            document.getElementById("tabela-sessoes-body").innerHTML =
                '<tr><td colspan="4" style="text-align:center;padding:24px;color:rgb(114 7 149 / 0.87);">' +
                'Erro ao carregar horários. Tente novamente.</td></tr>';
        });
}

function fecharModal() {
    document.getElementById("modal-agendamento").style.display = "none";
}

function bloquearBotoesModal(botaoClicado, textoCarregamento) {
    const botoes = document.querySelectorAll("#tabela-sessoes-body button");
    botoes.forEach(btn => {
        btn.disabled = true;
        btn.style.cursor = "not-allowed";
        btn.style.opacity = "0.65";
    });

    if (botaoClicado) {
        botaoClicado.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> ${textoCarregamento}`;
        botaoClicado.style.opacity = "0.9";
    }
}

function restaurarBotaoModal(botaoClicado, textoOriginal) {
    const botoes = document.querySelectorAll("#tabela-sessoes-body button");
    botoes.forEach(btn => {
        if (!btn.classList.contains("ssp-btn-disabled")) {
            btn.disabled = false;
            btn.style.cursor = "pointer";
            btn.style.opacity = "1";
        }
    });

    if (botaoClicado) {
        botaoClicado.innerHTML = textoOriginal;
    }
}

// Executa a ação de Agendar
function confirmarAgendamento(idSessao, botaoClicado) {
    if (requisicaoEmAndamento) return;
    requisicaoEmAndamento = true;

    const textoOriginal = botaoClicado ? botaoClicado.innerHTML : "Agendar ➔";
    bloquearBotoesModal(botaoClicado, "Processando...");

    fetch(`/api/agendamento/${idSessao}/agendar`, { method: 'POST' })
        .then(response => {
            if (response.status === 401) {
                alert("Para agendar um horário, você precisa entrar na sua conta.");
                window.location.href = "/login";
                throw new Error("Usuário não autenticado");
            }
            if (response.ok) return response.text();
            return response.text().then(text => { throw new Error(text) });
        })
        .then(mensagem => {
            alert(mensagem);
            const idServicoAtual = document.getElementById("modal-agendamento").dataset.idServicoAtual;
            abrirModalAgendamento(idServicoAtual);
        })
        .catch(error => {
            if (error.message !== "Usuário não autenticado") {
                alert(error.message);
                restaurarBotaoModal(botaoClicado, textoOriginal);
            }
        })
        .finally(() => {
            requisicaoEmAndamento = false;
        });
}

// Executa a ação de Desfazer o Agendamento
function desfazerAgendamento(idSessao, botaoClicado) {
    if (requisicaoEmAndamento) return;

    if (!confirm("Tem certeza que deseja cancelar este agendamento?")) {
        return;
    }

    requisicaoEmAndamento = true;
    const textoOriginal = botaoClicado ? botaoClicado.innerHTML : "✕ Desfazer";
    bloquearBotoesModal(botaoClicado, "Cancelando...");

    fetch(`/api/agendamento/${idSessao}/cancelar`, { method: 'POST' })
        .then(response => {
            if (response.status === 401) {
                alert("Sua sessão expirou. Por favor, faça login novamente.");
                window.location.href = "/login";
                throw new Error("Usuário não autenticado");
            }
            if (response.ok) return response.text();
            return response.text().then(text => { throw new Error(text) });
        })
        .then(mensagem => {
            alert(mensagem);
            const idServicoAtual = document.getElementById("modal-agendamento").dataset.idServicoAtual;
            abrirModalAgendamento(idServicoAtual);
        })
        .catch(error => {
            if (error.message !== "Usuário não autenticado") {
                alert("Erro ao cancelar: " + error.message);
                restaurarBotaoModal(botaoClicado, textoOriginal);
            }
        })
        .finally(() => {
            requisicaoEmAndamento = false;
        });
}