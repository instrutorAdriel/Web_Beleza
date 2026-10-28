package com.app.beleza.controller;

import com.app.beleza.model.dto.DepoimentoDTO;
import com.app.beleza.model.dto.ServicoDisponibilidadeDTO;
import com.app.beleza.service.HomeService;
import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/")
    public String exibirHome(
            @RequestParam(required = false, defaultValue = "TODAS") String unidade,
            @RequestParam(required = false, defaultValue = "TODOS") String servico,
            HttpSession session,
            Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioNome", usuario != null ? usuario.getNomeUsuario() : null);

        // Pega a lista de procedimentos (usada na sessão de serviços e nos filtros)
        List<ServicoDisponibilidadeDTO> procedimentos = homeService.listarProcedimentosPorUnidade();
        model.addAttribute("procedimentos", procedimentos);

        // Busca os 15 depoimentos (sem paginação)
        Page<DepoimentoDTO> paginaDepoimentos = homeService.listarDepoimentosRecentes(unidade, servico);
        model.addAttribute("depoimentos", paginaDepoimentos.getContent());
        model.addAttribute("filtroUnidadeAtual", unidade);
        model.addAttribute("filtroServicoAtual", servico);

        // Gera as listas únicas para os filtros
        List<String> listaUnidades = procedimentos.stream()
                .map(ServicoDisponibilidadeDTO::getUnidade)
                .distinct()
                .sorted()
                .toList();

        List<String> listaServicos = procedimentos.stream()
                .map(ServicoDisponibilidadeDTO::getNomeServico)
                .distinct()
                .sorted()
                .toList();

        model.addAttribute("listaUnidadesFiltro", listaUnidades);
        model.addAttribute("listaServicosFiltro", listaServicos);

        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}