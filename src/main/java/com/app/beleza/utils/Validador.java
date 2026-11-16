package com.app.beleza.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;
import javax.naming.Context;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.Attributes;
import java.util.Hashtable;

public class Validador {

    private static final Set<String> DOMINIOS_PESSOAIS = Set.of(
            "gmail.com", "googlemail.com", "hotmail.com", "hotmail.com.br", "outlook.com",
            "outlook.com.br", "live.com", "live.com.br", "msn.com", "yahoo.com", "yahoo.com.br",
            "icloud.com", "me.com", "mac.com", "aol.com", "proton.me", "protonmail.com",
            "uol.com.br", "bol.com.br", "terra.com.br", "ig.com.br", "zoho.com", "gmx.com",
            "gmx.net", "fastmail.com", "hey.com"
    );

    // Regex 100% compatível com qualquer versão do Java (8, 11, 17, 21+)
    private static final String EMOJI_REGEX = "[\\x{1F600}-\\x{1F64F}\\x{1F300}-\\x{1F5FF}\\x{1F680}-\\x{1F6FF}\\x{1F700}-\\x{1F77F}\\x{1F780}-\\x{1F7FF}\\x{1F800}-\\x{1F8FF}\\x{1F900}-\\x{1F9FF}\\x{1FA00}-\\x{1FA6F}\\x{1FA70}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{FE00}-\\x{FE0F}]";
    private static final Pattern PATTERN_EMOJI = Pattern.compile(EMOJI_REGEX);

    // Mantido original
    public static boolean isEmailValido(String email){
        if (email == null || email.isBlank()) {
            return false;
        }

        // Se o e-mail contiver Punycode (conversão de emoji em domínio) ou emoji direto, considera inválido
        if (email.toLowerCase().contains("xn--") || contemEmoji(email)) {
            return false;
        }

        final String regexEmail = "^(.+)@(.+)$";
        final Pattern patternEmail = Pattern.compile(regexEmail);
        final Matcher matcherEmail = patternEmail.matcher(email);

        try {
            return matcherEmail.matches();
        } catch (InputMismatchException e){
            return false;
        }
    }

    /** Aceita somente provedores pessoais conhecidos e confirma que o domínio publica MX. */
    public static boolean isEmailPessoalComDominioValido(String email) {
        if (!isEmailValido(email)) return false;
        String[] partes = email.trim().toLowerCase().split("@", -1);
        if (partes.length != 2 || partes[0].isBlank() || !DOMINIOS_PESSOAIS.contains(partes[1])) return false;

        Hashtable<String, String> ambiente = new Hashtable<>();
        ambiente.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        ambiente.put("com.sun.jndi.dns.timeout.initial", "2500");
        ambiente.put("com.sun.jndi.dns.timeout.retries", "1");
        try {
            InitialDirContext dns = new InitialDirContext(ambiente);
            try {
                Attributes atributos = dns.getAttributes(partes[1], new String[]{"MX"});
                return atributos.get("MX") != null && atributos.get("MX").size() > 0;
            } finally {
                dns.close();
            }
        } catch (Exception e) {
            return false;
        }
    }

    // ATUALIZADO: agora espera formato ISO (yyyy-MM-dd), padrão enviado pelo <input type="date">
    public static boolean isDataNascimentoValido(String data_nascimento) {
        if (data_nascimento == null || data_nascimento.isBlank()) {
            return false;
        }

        try {
            LocalDate data = LocalDate.parse(data_nascimento); // formato ISO yyyy-MM-dd
            LocalDate hoje = LocalDate.now();
            LocalDate idadeMinima = hoje.minusYears(14);

            boolean depoisDoLimiteAntigo = data.isAfter(LocalDate.of(1901, 1, 1));
            boolean temIdadeMinima = !data.isAfter(idadeMinima); // precisa ter nascido até 14 anos atrás

            return depoisDoLimiteAntigo && temIdadeMinima;
        } catch (Exception e) {
            return false;
        }
    }

    // Método de verificação de emojis
    public static boolean contemEmoji(String texto) {
        if (texto == null || texto.isBlank()) {
            return false;
        }
        Matcher matcher = PATTERN_EMOJI.matcher(texto);
        return matcher.find();
    }
}
