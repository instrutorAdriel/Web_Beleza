package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class homeController {
    @GetMapping
    public String menssagem(){
        return "<!DOCTYPE html>\n" +
                "<html lang=\"pt-BR\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>validação</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <form id=\"form-login\">\n" +
                "        <label for=\"login-email\">E-mail:</label>\n" +
                "        <input type=\"email\" id=\"login-email\">\n" +
                "    \n" +
                "        <label for=\"login-senha\">Senha:</label>\n" +
                "        <input type=\"password\" id=\"login-senha\">\n" +
                "    \n" +
                "        <button type=\"submit\">Entrar</button>\n" +
                "    </form>\n" +
                "    \n" +
                "    <script>\n" +
                "        document.getElementById('form-login').addEventListener('submit', function(event) {\n" +
                "            // Evita que a página recarregue ao clicar em \"Entrar\"\n" +
                "            event.preventDefault(); \n" +
                "            \n" +
                "            // Pega o que o usuário digitou\n" +
                "            const email = document.getElementById('login-email').value;\n" +
                "            const senha = document.getElementById('login-senha').value;\n" +
                "            \n" +
                "            // Validação simples\n" +
                "            if (email === \"\" || senha === \"\") {\n" +
                "                alert(\"Por favor, preencha todos os campos.\");\n" +
                "            } else {\n" +
                "                alert(`Login enviado!\\nEmail: ${email}`);\n" +
                "            }\n" +
                "        });\n" +
                "    </script>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }
}
