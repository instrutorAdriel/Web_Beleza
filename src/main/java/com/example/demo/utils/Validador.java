package com.example.demo.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validador {
    public static boolean isEmailValido(String email){
        final String regexEmail = "^(.+)@(.+)$";
        final Pattern patternEmail = Pattern.compile(regexEmail);
        final Matcher matcherEmail = patternEmail.matcher(email);

        try {
            return matcherEmail.matches();
        } catch (InputMismatchException e){
            return false;
        }
    }

    public static boolean isDataNascimentoValido(String data_nascimento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(data_nascimento, formatter).isAfter(LocalDate.of(1901, 1, 1));
    }
}
