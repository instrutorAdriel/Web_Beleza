package com.app.beleza.controller;

import com.app.beleza.model.SessaoAtendimentoDTO;
import com.app.beleza.model.Usuario;
import com.app.beleza.service.SessaoAtendimentoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Controller // Usamos @Controller para renderizar fragmentos HTML
@RequestMapping("/api/agendamento")
public class AgendamentoController {

    @Autowired
    private SessaoAtendimentoService service;

    // 1. Retorna o pedaço de HTML da tabela com os botões calculados pelo Java
    @GetMapping("/modal-tabela")
    public String carregarTabelaModal(@RequestParam Long servicoId, Model model, HttpSession session) {
        // Pega quem está navegando na sessão atual
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Passa o usuário logado para a service calcular se aquela vaga é 'deste' usuário
        List<SessaoAtendimentoDTO> sessoes = service.listarPorServico(servicoId, usuarioLogado);

        model.addAttribute("sessoes", sessoes);
        return "home :: tabela-modal-fragment";
    }

    @PostMapping("/{id}/agendar")
    @ResponseBody
    public ResponseEntity<String> agendar(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para realizar um agendamento.");
        }

        try {
            // PASSAMOS O USUÁRIO LOGADO PARA A SERVICE
            service.agendarSessao(id, usuarioLogado);
            return ResponseEntity.ok("Agendamento Realizado com Sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<String> cancelar(@PathVariable Long id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para cancelar.");
        }

        try {
            service.cancelarSessao(id, usuarioLogado);
            return ResponseEntity.ok("Agendamento cancelado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}