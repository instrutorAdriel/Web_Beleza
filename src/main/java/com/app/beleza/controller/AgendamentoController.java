package com.app.beleza.controller;

import com.app.beleza.model.Usuario;
import com.app.beleza.model.dto.SessaoAtendimentoDTO;
import com.app.beleza.service.AgendamentoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/agendamento")
public class AgendamentoController {

    @Autowired
    private AgendamentoService agendamentoService;

    @GetMapping("/modal-tabela")
    public String carregarTabelaModal(@RequestParam Integer servicoId, Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        List<SessaoAtendimentoDTO> sessoes = agendamentoService.listarPorDisponibilidade(servicoId, usuarioLogado);
        model.addAttribute("sessoes", sessoes);
        return "home :: tabela-modal-fragment";
    }

    @PostMapping("/{id}/agendar")
    @ResponseBody
    public ResponseEntity<String> agendar(@PathVariable Integer id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para agendar.");
        }
        try {
            agendamentoService.agendar(id, usuarioLogado);
            return ResponseEntity.ok("Agendamento realizado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<String> cancelar(@PathVariable Integer id, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return ResponseEntity.status(401).body("Você precisa estar logado para cancelar.");
        }
        try {
            agendamentoService.cancelarPorDisponibilidade(id, usuarioLogado);
            return ResponseEntity.ok("Agendamento cancelado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}