package com.app.beleza.controller;

import com.app.beleza.model.Depoimento;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.service.HomeService;
import com.app.beleza.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class HomeController {

    @Autowired
    private HomeService homeService;
    @Autowired
    private DepoimentoRepository depoimentoRepository;

    // Esta continua sendo a página inicial do seu sistema (http://localhost:8080/)
    @GetMapping("/")
    public String exibirHome(HttpSession session,Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("servicos", homeService.listarServicos());
        model.addAttribute("depoimentos", homeService.listarDepoimentos());
        model.addAttribute("servicos", homeService.listarServicos());
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuarioNome", usuario != null ? usuario.getNomeCompleto() : null);

        return "home";
    }

    @GetMapping("/indefinido")
    public String exibirTelaIndefinida(Model model){
        model.addAttribute("tituloPagina", "Página Indefinida");
        return "undefined";
    }
        @PostMapping("/perfil")
    @ResponseBody
    public ResponseEntity<?> salvarDepoimento(
            @RequestParam String nome,
            @RequestParam String servico,
            @RequestParam String unidade,
            @RequestParam String texto,
            @RequestParam(required = false) MultipartFile foto1,
            @RequestParam(required = false) MultipartFile foto2
    ) {
        try {
            Depoimento depoimento = new Depoimento();
            depoimento.setNome(nome);
            depoimento.setServico(servico);
            depoimento.setUnidade(unidade);
            depoimento.setTexto(texto);

            if (foto1 != null && !foto1.isEmpty()) {
                String nomeArquivo1 = System.currentTimeMillis() + "_1_" + foto1.getOriginalFilename();
                Path caminho1 = Paths.get("uploads/depoimentos/" + nomeArquivo1);
                Files.createDirectories(caminho1.getParent());
                Files.write(caminho1, foto1.getBytes());
                depoimento.setImagem1(nomeArquivo1);
            }

            if (foto2 != null && !foto2.isEmpty()) {
                String nomeArquivo2 = System.currentTimeMillis() + "_2_" + foto2.getOriginalFilename();
                Path caminho2 = Paths.get("uploads/depoimentos/" + nomeArquivo2);
                Files.createDirectories(caminho2.getParent());
                Files.write(caminho2, foto2.getBytes());
                depoimento.setImagem2(nomeArquivo2);
            }

            depoimentoRepository.save(depoimento);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar depoimento");
        }
    }
}