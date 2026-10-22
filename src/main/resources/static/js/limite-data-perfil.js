document.addEventListener("DOMContentLoaded", () => {
    const campoData = document.getElementById("dataNascimento");
    if (!campoData || campoData.type !== "date") return;

    const ajustarLimites = () => {
        const partes = campoData.value.match(/^(\d{4})-(\d{2})-(\d{2})$/);
        if (!partes) return;

        let ano = Number(partes[1]);
        const mes = Number(partes[2]);
        let dia = Number(partes[3]);
        const anoMinimo = Number(campoData.min.slice(0, 4));
        const anoMaximo = Number(campoData.max.slice(0, 4));

        // Corrige somente o ano, preservando mês e dia sempre que existirem.
        ano = Math.max(anoMinimo, Math.min(anoMaximo, ano));
        const ultimoDiaDoMes = new Date(ano, mes, 0).getDate();
        dia = Math.min(dia, ultimoDiaDoMes);

        let ajustada = `${ano}-${String(mes).padStart(2, "0")}-${String(dia).padStart(2, "0")}`;
        // No ano atual, uma data futura fica limitada ao dia máximo de hoje.
        if (ajustada > campoData.max) ajustada = campoData.max;
        if (ajustada < campoData.min) ajustada = campoData.min;
        campoData.value = ajustada;
    };

    // Espera o usuário terminar de editar todos os segmentos do input date.
    campoData.addEventListener("blur", ajustarLimites);
    ajustarLimites();
});
