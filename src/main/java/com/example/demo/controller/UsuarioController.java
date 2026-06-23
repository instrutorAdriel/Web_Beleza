package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioDTO;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // O método exibirHome que usava @GetMapping("/") foi REMOVIDO daqui
    // para não conflitar com a HomeController.

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
            model.addAttribute("usuarioDTO", form);
            model.addAttribute("tituloPagina", "Entrar");
            return "login";
        }

        return "redirect:/painel";
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

        return "redirect:/login";
    }

    /* ─── RECUPERAR SENHA ─────────────────────────────────────────────────── */
    @GetMapping("/recuperar-senha")
    public String exibirRecuperarSenha(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Recuperar Senha");
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarRecuperarSenha(@ModelAttribute UsuarioDTO form, Model model) {
        return "redirect:/codigo";
    }

    /* ─── CÓDIGO DE VERIFICAÇÃO ───────────────────────────────────────────── */
    @GetMapping("/codigo")
    public String exibirCodigo(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Verificar Código");
        return "codigo";
    }

    @PostMapping("/verificar-codigo")
    public String processarCodigo(@ModelAttribute UsuarioDTO form, Model model) {
        if ("123456".equals(form.getCodigoVerificacao())) {
            return "redirect:/alterar-senha";
        }
        model.addAttribute("erro", "Código de verificação incorreto ou expirado.");
        model.addAttribute("tituloPagina", "Verificar Código");
        return "codigo";
    }

    /* ─── ALTERAR SENHA ────────────────────────────────────────────────────── */
    @GetMapping("/alterar-senha")
    public String exibirAlterarSenha(Model model){
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Alterar Senha");
        return "altera-senha";
    }

    @PostMapping("/alterar-senha")
    public String processarAlterarSenha(@ModelAttribute UsuarioDTO form, Model model){
        String erro = usuarioService.alterarSenha(form);

        if (erro != null) {
            model.addAttribute("erro", erro);
            model.addAttribute("tituloPagina", "Alterar Senha");
            return "altera-senha";
        }
        return "redirect:/login";
    }
}