// Abre o modal e pede ao Spring o HTML pronto da tabela
function abrirModalAgendamento(idServico) {
    console.log("Carregando tabela via Spring para o Serviço:", idServico);

    // Armazena o ID do serviço atual no próprio modal para sabermos qual atualizar depois
    document.getElementById("modal-agendamento").dataset.idServicoAtual = idServico;

    document.getElementById("modal-agendamento").style.display = "flex";

    // Busca o fragmento HTML renderizado pelo servidor
    fetch(`/api/agendamento/modal-tabela?servicoId=${idServico}`)
        .then(response => response.text()) //
        .then(htmlDoFragmento => {

            document.getElementById("tabela-sessoes-body").innerHTML = htmlDoFragmento;
        })
        .catch(error => console.error("Erro ao carregar fragmento:", error));
}

function fecharModal() {
    document.getElementById("modal-agendamento").style.display = "none";
}

// Executa a ação de Agendar
function confirmarAgendamento(idSessao) {
    fetch(`/api/agendamento/${idSessao}/agendar`, { method: 'POST' })
        .then(response => {
            if (response.ok) return response.text();
            return response.text().then(text => { throw new Error(text) });
        })
        .then(mensagem => {
            alert(mensagem);


            const idServicoAtual = document.getElementById("modal-agendamento").dataset.idServicoAtual;
            abrirModalAgendamento(idServicoAtual);
        })
        .catch(error => alert("Erro ao agendar: " + error.message));
}

// Executa a ação de Desfazer o Agendamento
function desfazerAgendamento(idSessao) {
    if (confirm("Tem certeza que deseja cancelar este agendamento?")) {
        fetch(`/api/agendamento/${idSessao}/cancelar`, { method: 'POST' })
            .then(response => {
                if (response.ok) return response.text();
                return response.text().then(text => { throw new Error(text) });
            })
            .then(mensagem => {
                alert(mensagem);


                const idServicoAtual = document.getElementById("modal-agendamento").dataset.idServicoAtual;
                abrirModalAgendamento(idServicoAtual);
            })
            .catch(error => alert("Erro ao cancelar: " + error.message));
    }
}