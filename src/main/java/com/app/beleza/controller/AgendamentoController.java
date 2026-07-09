package com.app.beleza.controller;

import com.app.beleza.model.SessaoAtendimentoDTO;
import com.app.beleza.service.SessaoAtendimentoService;
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
    public String carregarTabelaModal(@RequestParam Long servicoId, Model model) {
        List<SessaoAtendimentoDTO> sessoes = service.listarPorServico(servicoId);
        model.addAttribute("sessoes", sessoes);
        return "home :: tabela-modal-fragment";
    }

    // 2. Endpoint para Confirmar Agendamento
    @PostMapping("/{id}/agendar")
    @ResponseBody
    public ResponseEntity<String> agendar(@PathVariable Long id) {
        try {
            service.agendarSessao(id);
            return ResponseEntity.ok("Agendamento Realizado com Sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. NOVO: Endpoint para Desfazer/Cancelar Agendamento
    @PostMapping("/{id}/cancelar")
    @ResponseBody
    public ResponseEntity<String> cancelar(@PathVariable Long id) {
        try {
            service.cancelarSessao(id); // Garanta que esse método exista na sua SessaoAtendimentoService para devolver +1 vaga
            return ResponseEntity.ok("Agendamento cancelado com sucesso!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}