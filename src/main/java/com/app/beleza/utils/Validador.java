package com.app.beleza.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validador {

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

    // Mantido original
    public static boolean isDataNascimentoValido(String data_nascimento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(data_nascimento, formatter).isAfter(LocalDate.of(1901, 1, 1));
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