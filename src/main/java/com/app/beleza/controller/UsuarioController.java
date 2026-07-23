package com.app.beleza.controller;

import com.app.beleza.model.Usuario;
import com.app.beleza.model.UsuarioDTO;
import com.app.beleza.respository.UsuarioRepository;
import com.app.beleza.service.AgendamentoService;
import com.app.beleza.service.PasswordResetService;
import com.app.beleza.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import com.app.beleza.utils.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgendamentoService agendamentoService;

    /* ─── LOGIN / AUTENTICAÇÃO ────────────────────────────────────────────── */
    @GetMapping("/login")
    public String exibirLogin(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Entrar");
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@ModelAttribute UsuarioDTO form, Model model,  HttpSession session){
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

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

    /* ─── CADASTRO ────────────────────────────────────────────────────────── */
    @GetMapping("/cadastro")
    public String exibirCadastro(Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Criar Conta");
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(@ModelAttribute UsuarioDTO form, Model model, HttpSession session) {
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

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
    public String exibirRecuperSenha(@ModelAttribute UsuarioDTO form, Model model, HttpSession session){
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

        model.addAttribute("tituloPagina", "Alterar Senha");
        model.addAttribute("usuarioDTO", form);
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model, HttpSession session){
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/";
        }

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
        if (passwordResetService.verificarToken(token) != null) {
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

    /*
    SEÇÃO PERFIL
     */

    @GetMapping("/perfil")
    public String exibirPerfil(@RequestParam(required = false) String aba, Model model, HttpSession session) {
        Usuario usuario = (Usuario)session.getAttribute("usuarioLogado");
        Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());

        if (resultado.isEmpty()) {
            return "redirect:/";
        }

        UsuarioDTO usuarioAtualizado = usuarioService.converterModelParaDTO(resultado.get());

        model.addAttribute("tituloPagina", "Bem-vindo " + usuarioAtualizado.getNomeCompleto());
        model.addAttribute("usuarioDTO", usuarioAtualizado);

        System.out.println("Param (/perfil) = " + aba);
        if (aba == null) {
            model.addAttribute("abaAtiva", "informacao");
        } else if (aba.equals("configuracao")) {
            model.addAttribute("abaAtiva", "configuracao");
        } else if (aba.equals("agendamento")) {
            model.addAttribute("abaAtiva", "agendamento");
            model.addAttribute("agendamentos", agendamentoService.listarPorUsuario(usuario));
        } else if (aba.equals("avaliacao")) {
            model.addAttribute("abaAtiva", "avaliacao");
        }

        return "perfil";
    }

    @PostMapping("/perfil/atualizar-perfil")
    public String atualizarPerfil(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes){
        Usuario usuario = (Usuario)session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        // Passo de validação dos campos
        String res = usuarioService.salvarUsuarioInfo(form);
        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil";
        }

        // Busca o usuario e atualiza o formulario
        Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());

        session.setAttribute("usuarioLogado", resultado.get());

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Informações atualizadas com sucesso!");
        redirectAttributes.addFlashAttribute("usuarioDTO", form);

        return "redirect:/perfil";
    }

    @PostMapping("/perfil/atualizar-senha")
    public String atualizarSenha(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes){
        Usuario usuario = (Usuario)session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        form.setEmail(usuario.getEmail());

        String res = usuarioService.atualizarSenha(form);
        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil?aba=configuracao";
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha atualizado com sucesso!");
        redirectAttributes.addFlashAttribute("tituloPagina", "Bem-vindo " + usuario.getNomeCompleto());
        redirectAttributes.addFlashAttribute("usuarioDTO", form);
        return "redirect:/perfil?aba=configuracao";
    }

    @PostMapping("/agendamentos/cancelar/{id}")
    public String cancelarAgendamento(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        boolean cancelado = agendamentoService.cancelar(id, usuario);

        if (cancelado) {
            redirectAttributes.addFlashAttribute("agendamentoMensagem", "Agendamento cancelado com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("agendamentoMensagem", "Não foi possível cancelar este agendamento.");
        }

        return "redirect:/perfil?aba=agendamento";
    }
}