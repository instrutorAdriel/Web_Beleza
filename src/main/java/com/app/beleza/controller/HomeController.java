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

        // Usuário logado
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioNome", homeService.obterNomeExibicao(usuario));

        // Procedimentos (uma única consulta ao banco)
        List<ServicoDisponibilidadeDTO> procedimentos = homeService.listarProcedimentosPorUnidade();
        model.addAttribute("procedimentos", procedimentos);

        // Unidades carregadas diretamente do banco
        List<String> unidades = homeService.listarNomesUnidades();
        model.addAttribute("unidades", unidades);
        model.addAttribute("listaUnidadesFiltro", unidades);

        // Depoimentos recentes
        Page<DepoimentoDTO> paginaDepoimentos = homeService.listarDepoimentosRecentes(unidade, servico);
        model.addAttribute("depoimentos", paginaDepoimentos.getContent());
        model.addAttribute("filtroUnidadeAtual", unidade);
        model.addAttribute("filtroServicoAtual", servico);

        // Lista única de serviços para o filtro de depoimentos
        List<String> listaServicos = procedimentos.stream()
                .map(ServicoDisponibilidadeDTO::getNomeServico)
                .distinct()
                .sorted()
                .toList();
        model.addAttribute("listaServicosFiltro", listaServicos);

        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model) {
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
}