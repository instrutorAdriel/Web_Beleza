document.getElementById('depoimento-form').addEventListener('submit', async function (e) {
    e.preventDefault();

    const erroDiv = document.getElementById('depoimento-erro');
    erroDiv.style.display = 'none';

    const formData = new FormData(this); // pega nome, servico, unidade, texto, foto1, foto2 automaticamente

    try {
        const response = await fetch('/depoimentos', {
            method: 'POST',
            body: formData
        });

        if (!response.ok) {
            throw new Error('Erro ao enviar depoimento');
        }

        alert('Depoimento enviado com sucesso!');
        this.reset();

    } catch (erro) {
        erroDiv.textContent = 'Não foi possível enviar seu depoimento. Tente novamente.';
        erroDiv.style.display = 'block';
    }
});