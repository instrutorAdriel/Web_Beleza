function togglePasswordVisibility(inputId, iconId) {
    const input = document.getElementById(inputId);
    const icone = document.getElementById(iconId);
    if (!input || !icone) return;

    if (input.type === "password") {
        // Senha visível -> olho riscado (clicar oculta)
        input.type = "text";
        icone.classList.replace("fa-eye", "fa-eye-slash");
    } else {
        // Senha oculta -> olho aberto (clicar mostra)
        input.type = "password";
        icone.classList.replace("fa-eye-slash", "fa-eye");
    }
}