package com.app.beleza.controller;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import com.app.beleza.model.dto.UsuarioDTO;
import com.app.beleza.model.enums.SituacaoAgendamento;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.ModeloRepository;
import com.app.beleza.respository.UsuarioRepository;
import com.app.beleza.service.CloudinaryService;
import com.app.beleza.service.PasswordResetService;
import com.app.beleza.service.UsuarioService;
import com.app.beleza.utils.Validador;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class UsuarioController {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    @Autowired
    private com.app.beleza.service.AgendamentoService agendamentoService;

    @Autowired
    private CloudinaryService cloudinaryService;

    /* ─── LOGIN / AUTENTICAÇÃO ────────────────────────────────────────────── */
    @GetMapping("/login")
    public String exibirLogin(Model model) {
        model.addAttribute("usuarioDTO", new UsuarioDTO());
        model.addAttribute("tituloPagina", "Entrar");
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@ModelAttribute UsuarioDTO form, Model model, HttpSession session) {
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

    // (sem mudanças) só exibe a página
    @GetMapping("/recuperar-senha")
    public String exibirRecuperSenha(@ModelAttribute UsuarioDTO form, Model model) {
        model.addAttribute("tituloPagina", "Alterar Senha");
        model.addAttribute("usuarioDTO", form);
        return "recuperar-senha";
    }

    // ALTERADO: redirect após o POST + aviso de e-mail já enviado
    @PostMapping("/recuperar-senha")
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model,
                                 RedirectAttributes redirect) {

        if (form.getEmail() == null || form.getEmail().isBlank()) {
            model.addAttribute("erro", "E-mail em branco");
            return "recuperar-senha";
        }

        if (!Validador.isEmailValido(form.getEmail())) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());
        if (res.isEmpty()) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        // Guarda o resultado para testar os 3 casos
        String resultado = passwordResetService.enviarEmailRecuperarSenha(form.getEmail(), res.get());

        // Já existe um e-mail válido: avisa para não clicar de novo
        if (PasswordResetService.EMAIL_JA_ENVIADO.equals(resultado)) {
            redirect.addFlashAttribute("erro",
                    "O e-mail já foi enviado. Verifique sua caixa de entrada e o spam. " +
                            "Não é necessário clicar novamente; você poderá pedir outro link em 30 minutos.");
            return "redirect:/recuperar-senha";
        }

        // Erro real (falha no e-mail, usuário não encontrado etc.)
        if (resultado != null) {
            model.addAttribute("erro", "Ocorreu um erro, tente novamente mais tarde");
            return "recuperar-senha";
        }

        // Sucesso: primeiro envio. O redirect evita reenvio do POST no F5
        redirect.addFlashAttribute("succ",
                "Foi enviado um e-mail com o link, verifique a caixa de mensagens ou spam.");
        return "redirect:/recuperar-senha";
    }

    // (sem mudanças)
    @GetMapping("/alterar-senha/{token}")
    public String exibirAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model) {
        if (passwordResetService.verificarToken(token) != null) {
            return "redirect:/indefinido";
        }

        model.addAttribute("token", token);
        model.addAttribute("usuarioDTO", form);

        return "alterar-senha";
    }

    // ALTERADO: devolve o token no erro e apaga o token após o sucesso
    @PostMapping("/alterar-senha/{token}")
    public String processarAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model) {
        if (passwordResetService.verificarToken(token) != null) {
            return "redirect:/indefinido";
        }

        form.setEmail(passwordResetService.retornarUsuario(token).getEmail());
        String res = usuarioService.alterarSenha(form);

        if (res != null) {
            model.addAttribute("erro", res);
            model.addAttribute("usuarioDTO", form);
            model.addAttribute("token", token);
            return "alterar-senha";
        }

        // O link não pode ser usado uma segunda vez
        passwordResetService.invalidarToken(token);

        model.addAttribute("succ", "Senha alterada com sucesso!");
        return "alterar-senha";
    }
}