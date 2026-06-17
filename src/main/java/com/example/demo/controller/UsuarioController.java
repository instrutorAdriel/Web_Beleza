package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioDTO;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /* ─── LOGIN / AUTENTICAÇÃO ────────────────────────────────────────────── */
    @GetMapping("/login")
    public String exibirLogin(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Entrar");
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@ModelAttribute UsuarioDTO form, Model model){
        System.out.println("=== TENTATIVA DE LOGIN ===");
        System.out.println("Email vindo do HTML: [" + form.getEmail() + "]");
        System.out.println("Senha vinda do HTML: [ PROTEGIDO ]");

        Usuario usuario = usuarioService.autenticar(form.getEmail(), form.getSenha());

        if (usuario == null) {
            model.addAttribute("erro", "E-mail ou senha incorretos.");
            model.addAttribute("usuarioDTO", form); // Preserva o e-mail na tela se errar
            model.addAttribute("tituloPagina", "Entrar");
            return "login";
        }

        // Sucesso! Redireciona para a rota da Home interna do Portal
        return "redirect:/usuario/home";
    }

    /* ─── TELA HOME DO PORTAL SENAC ───────────────────────────────────────── */
    @GetMapping("/home")
    public String exibirHome(Model model) {
        model.addAttribute("tituloPagina", "Home - Portal Senac");
        // Retorna o arquivo 'home.html' em src/main/resources/templates/
        return "home";
    }

    /* ─── CADASTRO ────────────────────────────────────────────────────────── */
    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Criar Conta");
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@ModelAttribute UsuarioDTO form, Model model) {
        String erro = usuarioService.cadastrar(form);

        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("usuarioDTO", form);
            model.addAttribute("tituloPagina", "Criar Conta");
            return "cadastro";
        }

        return "redirect:/usuario/login";
    }

    /*
    Rota de recuperar a senha e alterar senha
     */
    @GetMapping("/recuperar-senha")
    public String exibirRecuperSenha(@ModelAttribute UsuarioDTO form, Model model){
        model.addAttribute("tituloPagina", "Alterar Senha");
        model.addAttribute("");
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model){

        return "altera-senha";
    }

    @GetMapping("/validar-token/{token}")
    public String validarToken(@PathVariable String token_uuid){

    }

    @GetMapping("/alterar-senha")
    public String exibirAlterarSenha(@RequestParam String token_uuid){
        return "altera-senha";
    }
}