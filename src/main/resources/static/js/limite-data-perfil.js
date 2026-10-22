document.addEventListener("DOMContentLoaded", () => {
    const campoData = document.getElementById("dataNascimento");
    if (!campoData || campoData.type !== "date") return;

    const anoAtual = new Date().getFullYear();
    const dataMinima = campoData.min ? new Date(`${campoData.min}T00:00:00`) : new Date(1900, 0, 1);
    const dataMaxima = campoData.max ? new Date(`${campoData.max}T00:00:00`) : new Date();
    dataMaxima.setHours(0, 0, 0, 0);

    const formatarData = (data) => {
        const ano = String(data.getFullYear()).padStart(4, "0");
        const mes = String(data.getMonth() + 1).padStart(2, "0");
        const dia = String(data.getDate()).padStart(2, "0");
        return `${ano}-${mes}-${dia}`;
    };

    const limitarData = () => {
        const valor = campoData.value;
        if (!valor) return;

        let data = new Date(`${valor}T00:00:00`);
        if (Number.isNaN(data.getTime())) return;

        if (data < dataMinima) {
            const anoMinimo = dataMinima.getFullYear();
            const ultimoDiaDoMes = new Date(anoMinimo, data.getMonth() + 1, 0).getDate();
            const dia = Math.min(data.getDate(), ultimoDiaDoMes);
            data = new Date(anoMinimo, data.getMonth(), dia);
            campoData.value = formatarData(data);
        } else if (data > dataMaxima) {
            data = new Date(dataMaxima);
            campoData.value = formatarData(data);
        }

        if (data.getFullYear() < dataMinima.getFullYear()) {
            campoData.value = formatarData(dataMinima);
        } else if (data.getFullYear() > anoAtual) {
            campoData.value = formatarData(dataMaxima);
        }

        campoData.setCustomValidity("");
    };

    let timerDigitacao;
    campoData.addEventListener("input", () => {
        campoData.setCustomValidity("");
        clearTimeout(timerDigitacao);
        timerDigitacao = setTimeout(limitarData, 700);
    });
    campoData.addEventListener("blur", () => {
        clearTimeout(timerDigitacao);
        limitarData();
    });

    // Corrige também um valor antigo fora do intervalo assim que o perfil é aberto.
    if (campoData.value) {
        limitarData();
    }
});
