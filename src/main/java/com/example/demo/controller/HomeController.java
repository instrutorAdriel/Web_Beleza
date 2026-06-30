package com.example.demo.controller;

import com.example.demo.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.service.HomeService;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    // Esta continua sendo a página inicial do seu sistema (http://localhost:8080/)
    @GetMapping("/")
    public String home(Model model) {

        // Títulos
        model.addAttribute("footerTituloSobre", "Sobre o Senac");
        model.addAttribute("footerTituloContato", "Contato rápido");
        model.addAttribute("footerTituloRedes", "Siga-nos");

        // Textos
        model.addAttribute("footerSobre", "O Serviço Nacional de Aprendizagem Comercial é o principal agente de educação profissional focado no sector de comércio de bens, serviços e turismo do país.");
        model.addAttribute("footerTelefone", "0800 707 1022");
        model.addAttribute("footerEmail", "faleconosco@senac.br");

        // Links das redes sociais
        model.addAttribute("footerFacebookLink", "https://www.facebook.com/SenacDistritoFederal");
        model.addAttribute("footerInstagramLink", "https://www.instagram.com/senacdf");
        model.addAttribute("footerLinkedinLink", "https://www.linkedin.com/authwall?trk=bf&trkInfo=AQGqL6ZdkqU37gAAAZ8Z2zwQWpIQrgnOX71DdJ-ibwaiyqDsFXphD3SfZEtbVgK_ArHQIO2Gw4FvzooMcrh0=&original_referer=&sessionRedirect=https%3A%2F%2Fwww.linkedin.com%2Fcompany%2Fsenacdf%2F");

        // Rodapé inferior
        model.addAttribute("footerAno", "2026");
        model.addAttribute("footerEmpresa", "Senac Brasil");

        model.addAttribute("servicos", homeService.listarServicos());
        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}