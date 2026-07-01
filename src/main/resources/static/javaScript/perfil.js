const perfil_informacao = document.getElementById("perfil-informacao");
const perfil_configuracao = document.getElementById("perfil-configuracao");
const perfil_agendamento = document.getElementById("perfil-agendamento");
const perfil_avaliacao = document.getElementById("perfil-avaliacao");

const sidebar_itens = document.querySelectorAll(".sidebar a.bar-item");
const sections = document.querySelectorAll("section");

sidebar_itens.forEach(bar_item => {
    bar_item.addEventListener('click', function (evento) {
        evento.preventDefault(); // Evita recarregar a página
        const bar_item_clicado = evento.currentTarget;

        sidebar_itens.forEach(s => s.classList.remove("active"));
        sections.forEach(s => s.classList.add("hidden"));

        const targetId = bar_item_clicado.getAttribute("data-target");
        const targetSection = document.getElementById(targetId);

        bar_item_clicado.classList.add("active");
        targetSection.classList.remove("hidden");
    })
})