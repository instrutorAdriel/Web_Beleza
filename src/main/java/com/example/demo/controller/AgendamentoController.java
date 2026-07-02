package com.example.demo.controller;

import com.example.demo.model.SessaoAtendimento;
import com.example.demo.model.SessaoAtendimentoDTO;
import com.example.demo.respository.SessaoAtendimentoRepository;
import com.example.demo.service.SessaoAtendimentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agendamento")
public class AgendamentoController {

    @Autowired
    private SessaoAtendimentoService service;

    @GetMapping

    public ResponseEntity<List<SessaoAtendimentoDTO>> listarPorServico(@RequestParam Long servicoId){
        List<SessaoAtendimentoDTO> dtos = service.listarPorServico(servicoId);
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{id}/agendar")
    public ResponseEntity<String> agendar(@PathVariable Long id){
        try{ service.agendarSessao(id);
            return ResponseEntity.ok("Agendamento Realizado com Sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
