package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioDTO;
import com.example.demo.respository.UsuarioRepository;
import com.example.demo.service.PasswordResetService;
import com.example.demo.service.UsuarioService;
import com.example.demo.utils.Validador;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /* ─── LOGIN / AUTENTICAÇÃO ────────────────────────────────────────────── */
    @GetMapping("/login")
    public String exibirLogin(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Entrar");
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@ModelAttribute UsuarioDTO form, Model model,  HttpSession session){
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

        // Sucesso! Redireciona para a rota da Home interna do Portal
        session.setAttribute("usuarioLogado", usuario);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // limpa toda a sessão
        return "redirect:/";
    }

    /*
    @GetMapping("/home")
    public String exibirHome(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("usuarioLogado", usuario);
        model.addAttribute("tituloPagina", "Home - Portal Senac");
        return "home";
    }
    */

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

    /*
    Rota de recuperar a senha e alterar senha
     */
    @GetMapping("/recuperar-senha")
    public String exibirRecuperSenha(@ModelAttribute UsuarioDTO form, Model model){
        model.addAttribute("tituloPagina", "Alterar Senha");
        model.addAttribute("usuarioDTO", form);
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model){
        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());

        if (form.getEmail().isBlank()){
            model.addAttribute("erro", "E-mail em branco");
            return "recuperar-senha";
        } else if (!Validador.isEmailValido(form.getEmail())){
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        } else if (res.isEmpty()){
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        Usuario usuario = res.get();

        if (passwordResetService.enviarEmailRecuperarSenha(form.getEmail(), usuario) == null){
            model.addAttribute("succ", "Foi enviado um e-mail com o link, verifique a caixa de mensagens ou spam.");
        } else {
            model.addAttribute("erro", "Ocorreu um erro, tente novamente mais tarde");
            return "recuperar-senha";
        }

        return "recuperar-senha";
    }

    @GetMapping("/alterar-senha/{token}")
    public String exibirAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model){
        if (passwordResetService.verificarToken(token) != null){
            return "redirect:/indefinido";
        }

        model.addAttribute("token", token);
        model.addAttribute("usuarioDTO", form);

        return "alterar-senha";
    }

    @PostMapping("/alterar-senha/{token}")
    public String processarAlterarSenha(@PathVariable String token, @ModelAttribute  UsuarioDTO form, Model model){
        // Vericação se o token do alterar senha é de fato valido antes de alterar a senha
        if (passwordResetService.verificarToken(token) != null){
            // Exibir uma mensagem na página dizendo que ocorreu um erro, sem necessidade de retornar para uma página
            // de erro

            return "redirect:/indefinido";
        }

        form.setEmail(passwordResetService.retornarUsuario(token).getEmail());
        String res = usuarioService.alterarSenha(form);

        if (res != null){
            model.addAttribute("erro", res);
            model.addAttribute("usuarioDTO", form);
            return "alterar-senha";
        }

        model.addAttribute("succ", "Senha alterada com sucesso!");

        return "alterar-senha";
    }
}