    package com.example.demo.controller;

    import com.example.demo.model.Musica;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RestController;

    @RestController
    @RequestMapping("/api")
    public class MusicaController {
        @GetMapping("/musica")
        public Musica buscarMusica() {
            // Retorna um objeto Musica instanciado com dados de exemplo
            return new Musica("Asa Branca", "Luiz Gonzaga", "O Rei do Baião");
        }
    }
