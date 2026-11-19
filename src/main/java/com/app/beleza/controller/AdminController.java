package com.app.beleza.controller;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Usuario;
import com.app.beleza.model.enums.StatusModeracaoDepoimento;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.service.AdminAuthorizationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DepoimentoRepository depoimentoRepository;
    private final AdminAuthorizationService adminAuthorizationService;

    public AdminController(DepoimentoRepository depoimentoRepository,
                           AdminAuthorizationService adminAuthorizationService) {
        this.depoimentoRepository = depoimentoRepository;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping
    public String exibirPainel(HttpSession session, Model model) {
        exigirAdministrador(session);
        model.addAttribute("depoimentosPendentes",
                depoimentoRepository.buscarPorStatusModeracao(StatusModeracaoDepoimento.PENDENTE));
        return "admin";
    }

    @PostMapping("/depoimentos/{id}/aprovar")
    public String aprovarDepoimento(@PathVariable Integer id, HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        exigirAdministrador(session);
        moderarDepoimento(id, StatusModeracaoDepoimento.APROVADO,
                "Depoimento aprovado e publicado.", redirectAttributes);
        return "redirect:/admin";
    }

    @PostMapping("/depoimentos/{id}/rejeitar")
    public String rejeitarDepoimento(@PathVariable Integer id, HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        exigirAdministrador(session);
        moderarDepoimento(id, StatusModeracaoDepoimento.REJEITADO,
                "Depoimento rejeitado e mantido fora do site.", redirectAttributes);
        return "redirect:/admin";
    }

    private void moderarDepoimento(Integer id, StatusModeracaoDepoimento novoStatus,
                                    String mensagemSucesso, RedirectAttributes redirectAttributes) {
        Optional<Depoimento> resultado = depoimentoRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Depoimento não encontrado.");
        }

        Depoimento depoimento = resultado.get();
        if (depoimento.getStatusModeracao() != StatusModeracaoDepoimento.PENDENTE) {
            redirectAttributes.addFlashAttribute("mensagemError",
                    "Este depoimento já foi moderado.");
            return;
        }

        depoimento.setStatusModeracao(novoStatus);
        depoimentoRepository.save(depoimento);
        redirectAttributes.addFlashAttribute("mensagemSucesso", mensagemSucesso);
    }

    private void exigirAdministrador(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Entre para acessar esta página.");
        }
        if (!adminAuthorizationService.isAdministrador(usuario)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso restrito à administração.");
        }
    }
}
