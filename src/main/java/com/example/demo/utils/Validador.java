package com.example.demo.utils;

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
}
