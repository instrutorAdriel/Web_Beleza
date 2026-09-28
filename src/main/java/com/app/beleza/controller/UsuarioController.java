package com.app.beleza.controller;

import com.app.beleza.model.Usuario;
import com.app.beleza.model.UsuarioDTO;
import com.app.beleza.respository.UsuarioRepository;
import com.app.beleza.service.PasswordResetService;
import com.app.beleza.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import com.app.beleza.utils.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Import do BCrypt
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class UsuarioController {

    // Instância do Encoder solicitada
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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
    public String processarLogin(@ModelAttribute UsuarioDTO form, Model model, HttpSession session){
        Usuario usuario = usuarioService.autenticar(form.getEmail(), form.getSenha());

        if (usuario == null) {
            model.addAttribute("erro", "E-mail ou senha incorretos.");
            model.addAttribute("usuarioDTO", form);
            model.addAttribute("tituloPagina", "Entrar");
            return "login";
        }

        session.setAttribute("usuarioLogado", usuario);
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    /* ─── CADASTRO ────────────────────────────────────────────────────────── */
    @GetMapping("/cadastro")
    public String exibirCadastro(Model model) {
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

    /* ─── RECUPERAR / ALTERAR SENHA POR TOKEN ─────────────────────────────── */
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
    public String processarAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model){
        if (passwordResetService.verificarToken(token) != null) {
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

    /* ─── SEÇÃO PERFIL (EXIBIÇÃO COM SUPORTE A ABAS E DTO) ─────────────────── */
    @GetMapping("/perfil")
    public String exibirPerfil(@RequestParam(required = false, defaultValue = "informacao") String aba,
                               @ModelAttribute("usuarioDTO") UsuarioDTO form,
                               Model model,
                               HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());

        if (resultado.isEmpty()) {
            return "redirect:/";
        }

        UsuarioDTO usuarioAtualizado = usuarioService.converterModelParaDTO(resultado.get());

        model.addAttribute("tituloPagina", "Bem-vindo " + usuarioAtualizado.getNomeCompleto());
        model.addAttribute("usuarioDTO", usuarioAtualizado);
        model.addAttribute("abaAtiva", aba);

        return "perfil";
    }

    /* ─── ATUALIZAR INFORMAÇÕES DO PERFIL ─────────────────────────────────── */
    @PostMapping("/perfil/atualizar-perfil")
    public String atualizarPerfil(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes){
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        String res = usuarioService.salvarUsuarioInfo(form);
        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil?aba=informacao";
        }

        Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());
        session.setAttribute("usuarioLogado", resultado.get());

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Informações atualizadas com sucesso!");
        redirectAttributes.addFlashAttribute("usuarioDTO", form);

        return "redirect:/perfil?aba=informacao";
    }

    /* ─── ATUALIZAR SENHA (COM VERIFICAÇÃO BCRYPT) ────────────────────────── */
    @PostMapping("/perfil/atualizar-senha")
    public String atualizarSenha(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        String senhaAtual = form.getSenha();
        String novaSenha = form.getNovaSenha();
        String confirmacaoSenha = form.getConfirmacaoSenha();

        // 1. VERIFICAÇÃO DE CAMPOS EM BRANCO
        if (senhaAtual == null || senhaAtual.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha atual não pode estar em branco.");
            return "redirect:/perfil?aba=configuracao";
        }

        if (novaSenha == null || novaSenha.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "A nova senha não pode estar em branco.");
            return "redirect:/perfil?aba=configuracao";
        }

        if (confirmacaoSenha == null || confirmacaoSenha.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "A confirmação da nova senha não pode estar em branco.");
            return "redirect:/perfil?aba=configuracao";
        }

        Optional<Usuario> usuarioBanco = usuarioRepository.findById(usuario.getId());

        if (!encoder.matches(senhaAtual, usuarioBanco.get().getSenha())) {
            redirectAttributes.addFlashAttribute("mensagemError", "Senha atual incorreta.");
            return "redirect:/perfil?aba=configuracao";
        }

        // 3. VERIFICAÇÃO SE AS SENHAS COINCIDEM
        if (!novaSenha.equals(confirmacaoSenha)) {
            redirectAttributes.addFlashAttribute("mensagemError", "A nova senha e a confirmar nova senha não bate.");
            return "redirect:/perfil?aba=configuracao";
        }

        // 4. REQUISITOS DA NOVA SENHA (UM POR UM)
        if (!novaSenha.matches(".*[A-Z].*")) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha nova deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial. (ex: @, #, !, $).");
            return "redirect:/perfil?aba=configuracao";
        }

        if (!novaSenha.matches(".*[a-z].*")) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha nova deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial. (ex: @, #, !, $).");
            return "redirect:/perfil?aba=configuracao";
        }

        if (!novaSenha.matches(".*[0-9].*")) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha nova deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial. (ex: @, #, !, $).");
            return "redirect:/perfil?aba=configuracao";
        }

        if (!novaSenha.matches(".*[!@#$%^&*(),.?\":{}|<>" + "_\\-+=\\[\\]\\\\/;'`~].*")) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha nova deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial. (ex: @, #, !, $).");
            return "redirect:/perfil?aba=configuracao";
        }


        // 5. ATRIBUIÇÃO E ATUALIZAÇÃO NO BANCO DE DADOS
        form.setEmail(usuario.getEmail());
        form.setSenha(novaSenha);
        form.setNovaSenha(novaSenha);

        String res = usuarioService.alterarSenha(form);

        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil?aba=configuracao";
        }

        // 6. SUCESSO
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
        return "redirect:/perfil?aba=configuracao";
    }
}