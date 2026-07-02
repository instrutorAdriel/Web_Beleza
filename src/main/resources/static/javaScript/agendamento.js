// Recebe o ID do serviço clicado
function abrirModalAgendamento(idServico) {
    document.getElementById("modal-agendamento").style.display = "flex";

    // Envia o ID na rota para a Controller receber no @RequestParam
    fetch(`/api/agendamento?servicoId=${idServico}`)
        .then(response => response.json())
        .then(sessoes => {
            const tbody = document.getElementById("tabela-sessoes-body");
            tbody.innerHTML = "";

            sessoes.forEach(sessao => {
                const tr = document.createElement("tr");

                let botaoAcao = "";
                if (sessao.agendadoPeloUsuario) {
                    botaoAcao = `<button class="btn-agendado" disabled style="background-color: #13382c; color: #2ecc71;">✓ Agendado</button>`;
                } else if (sessao.vagasDisponiveis <= 0) {
                    botaoAcao = `<button class="btn-confirmar" disabled>Indisponível</button>`;
                } else {
                    botaoAcao = `<button class="btn-confirmar" onclick="confirmarAgendamento(${sessao.id})" style="background-color: #f39c12; color: white;">Agendar ➔</button>`;
                }

                tr.innerHTML = `
                    <td>${sessao.dataAtendimento}</td>
                    <td>${sessao.horarioInicial}</td>
                    <td style="color: ${sessao.vagasDisponiveis === 0 ? '#ef4444' : '#2ecc71'}; font-weight: bold;">
    ${sessao.vagasDisponiveis === 0 ? 'Esgotado' : sessao.vagasDisponiveis + ' restantes'}
</td>
                    <td>${botaoAcao}</td>
                `;

                tbody.appendChild(tr);
            });
        })
        .catch(error => console.error("Erro ao buscar sessões:", error));
}

function fecharModal() {
    document.getElementById("modal-agendamento").style.display = "none";
}