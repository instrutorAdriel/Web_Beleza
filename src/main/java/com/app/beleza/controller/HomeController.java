package com.app.beleza.controller;

import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
import com.app.beleza.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.service.HomeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    // Esta continua sendo a página inicial do seu sistema (http://localhost:8080/)
    @GetMapping("/")
    public String home(HttpSession session,Model model) {

        // Títulos
        model.addAttribute("footerTituloSobre", "Sobre o Senac");
        model.addAttribute("footerTituloSobre", "Sobre o Salão de beleza Senac");
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

        model.addAttribute("servicos", homeService.listarServicos());
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
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