package com.app.beleza.controller;

<<<<<<< HEAD
import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
import com.app.beleza.service.HomeService;
=======
import com.app.beleza.service.HomeService;
import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    // Esta continua sendo a página inicial do seu sistema (http://localhost:8080/)
    @GetMapping("/")
<<<<<<< HEAD
    public String home(HttpSession session,Model model) {
        model.addAttribute("servicos", homeService.listarServicos());
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
=======
    public String exibirHome(HttpSession session,Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("servicos", homeService.listarServicos());
        model.addAttribute("depoimentos", homeService.listarDepoimentos());
        model.addAttribute("servicos", homeService.listarServicos());
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioNome", usuario != null ? usuario.getNomeCompleto() : null);

        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}