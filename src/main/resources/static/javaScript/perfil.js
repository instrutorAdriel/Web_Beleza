    function configurarPreviewFoto(inputId, previewId) {
    const input = document.getElementById(inputId);
    const preview = document.getElementById(previewId);
    if (!input || !preview) return;

    input.addEventListener('change', function (event) {
    const arquivo = event.target.files[0];
    if (arquivo) {
    const leitor = new FileReader();
    leitor.onload = function (e) {
    preview.src = e.target.result;
    preview.style.display = 'block';
};
    leitor.readAsDataURL(arquivo);
} else {
    preview.style.display = 'none';
}
});
}
    configurarPreviewFoto('dep-foto1', 'dep-foto1-preview');
    configurarPreviewFoto('dep-foto2', 'dep-foto2-preview');

    const inputTelefone = document.getElementById("telefone");
    if (inputTelefone) {
    inputTelefone.addEventListener("input", function (e) {
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
}

    const formDepoimento = document.getElementById('depoimento-form');
    if (formDepoimento) {
    formDepoimento.addEventListener('submit', async function (e) {
        e.preventDefault();

        const form = this;
        const btnEnviar = document.getElementById('depoimento-btn-enviar');
        const erroDiv = document.getElementById('depoimento-erro');

        erroDiv.style.display = 'none';
        erroDiv.textContent = '';
        btnEnviar.disabled = true;
        btnEnviar.textContent = 'Enviando...';

        const formData = new FormData(form);

        try {
            const response = await fetch('/perfil/depoimento', {
                method: 'POST',
                body: formData
            });

            if (!response.ok) {
                throw new Error('Erro ao enviar depoimento');
            }

            form.style.display = 'none';
            document.getElementById('depoimento-sucesso').style.display = 'block';

        } catch (erro) {
            erroDiv.textContent = erro.message || 'Não foi possível enviar seu depoimento. Tente novamente.';
            erroDiv.style.display = 'block';
        } finally {
            btnEnviar.disabled = false;
            btnEnviar.textContent = 'Enviar depoimento';
        }
    });
}