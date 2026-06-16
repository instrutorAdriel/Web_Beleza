package com.example.demo.controller;

import com.example.demo.model.aluno;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping
public class AlunoController {

    @GetMapping("/api/aluno")
    public aluno buscarAluno(){
        return new aluno ("Uva Tavares Silva", "Tecnico de suco");
    }
}
