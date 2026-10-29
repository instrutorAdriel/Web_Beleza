package com.app.beleza.controller;

import com.app.beleza.service.HomeService;
import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/")
    public String exibirHome(HttpSession session,Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioNome", homeService.obterNomeExibicao(usuario));
        model.addAttribute("procedimentos", homeService.listarProcedimentosPorUnidade());
        model.addAttribute("depoimentos", homeService.listarDepoimentos());

        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}