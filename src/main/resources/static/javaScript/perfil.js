const perfil_informacao = document.getElementById("perfil-informacao");
const perfil_agendamento = document.getElementById("perfil-agendamento");
const perfil_avaliacao = document.getElementById("perfil-avaliacao");

const sidebar_itens = document.querySelectorAll(".sidebar a.bar-item");
const sections = document.querySelectorAll("section");

sidebar_itens.forEach(bar_item => {
    bar_item.addEventListener('click', function (evento) {
        const href = bar_item.getAttribute("href");
        if (href && !href.startsWith("#") && href !== ""){
            return;
        }

        evento.preventDefault(); // Evita recarregar a página
        const bar_item_clicado = evento.currentTarget;

        sidebar_itens.forEach(s => s.classList.remove("active"));
        sections.forEach(s => s.classList.add("hidden"));

        const targetId = bar_item_clicado.getAttribute("data-target");
        const targetSection = document.getElementById(targetId);

        bar_item_clicado.classList.add("active");
        targetSection.classList.remove("hidden");
    })
});

function ativarAbaPorHash() {
    const hash = window.location.hash.replace('#', '');
    if (!hash) return;

    const targetSection = document.getElementById(hash);
    const barItem = document.querySelector(`.sidebar a.bar-item[data-target="${hash}"]`);

    if (!targetSection || !barItem) return;

    sidebar_itens.forEach(s => s.classList.remove('active'));
    sections.forEach(s => s.classList.add('hidden'));

    barItem.classList.add('active');
    targetSection.classList.remove('hidden');
}

function confirmarCancelamentoAgendamentos() {
    const formularios = document.querySelectorAll('.form-cancelar-agendamento');

    formularios.forEach(form => {
        form.addEventListener('submit', function (e) {
            const confirmado = window.confirm('Tem certeza que deseja cancelar este agendamento?');
            if (!confirmado) {
                e.preventDefault();
            }
        });
    });
}

document.addEventListener('DOMContentLoaded', function () {
    ativarAbaPorHash();
    confirmarCancelamentoAgendamentos();

    document.getElementById("data_nascimento").addEventListener('input', function (e) {
        let num = e.target.value.replace(/\D/g, "");
        if (num.length > 8) num = num.substring(0, 8);

        if (num.length > 4) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2, 4)}/${num.substring(4)}`;
        } else if (num.length > 2) {
            e.target.value = `${num.substring(0, 2)}/${num.substring(2)}`;
        } else {
            e.target.value = num;
        }
    });

    document.getElementById("telefone").addEventListener("input", function (e) {
        // Remove tudo que não for número
        let num = e.target.value.replace(/\D/g, "");
        if (num.length > 11) {
            num = num.substring(0, 11);
        }
        if (num.length > 6) {
            e.target.value = `(${num.substring(0, 2)}) ${num.substring(2, 7)}-${num.substring(7)}`;
        } else if (num.length > 2) {
            e.target.value = `(${num.substring(0, 2)}) ${num.substring(2)}`;
        } else if (num.length > 0) {
            e.target.value = `(${num}`;
        } else {
            e.target.value = "";
        }
    });
});