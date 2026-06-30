package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        // Títulos
        model.addAttribute("footerTituloSobre", "Sobre o Salão de beleza Senac");
        model.addAttribute("footerTituloContato", "Contato rápido");
        model.addAttribute("footerTituloRedes", "Siga-nos");

        // Textos
        model.addAttribute("footerSobre", "O Salão de Beleza do SENAC-DF oferece serviços gratuitos à comunidade, proporcionando atendimento de qualidade, cuidado e bem-estar.");
        model.addAttribute("footerHorario", "Horário de funcionamento: segunda a sexta-feira | 8h às 19h");

        // Links
        model.addAttribute("footerWhatsappLink", "https://api.whatsapp.com/send/?phone=556137719800");
        model.addAttribute("footerFacebookLink", "https://www.facebook.com/SenacDistritoFederal/");
        model.addAttribute("footerInstagramLink", "https://www.instagram.com/senacdf/?hl=pt");
        model.addAttribute("footerLinkedinLink", "https://br.linkedin.com/school/senacdf/");

        // Rodapé inferior
        model.addAttribute("footerAno", "2026");
        model.addAttribute("footerEmpresa", "Senac Brasil");

        return "home";
    }
}