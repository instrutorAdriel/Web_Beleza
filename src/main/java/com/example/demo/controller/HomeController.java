package com.example.demo.controller;

import com.example.demo.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    // Esta continua sendo a página inicial do seu sistema (http://localhost:8080/)
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("servicos", homeService.listarServicos());
        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}