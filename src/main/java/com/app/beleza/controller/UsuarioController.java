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
    @GetMapping("/recuperar-senha")
    public String exibirRecuperSenha(@ModelAttribute UsuarioDTO form, Model model) {
        model.addAttribute("tituloPagina", "Alterar Senha");
        model.addAttribute("usuarioDTO", form);
        return "recuperar-senha";
    }

    @PostMapping("/recuperar-senha")
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model,
                                 RedirectAttributes redirect) {   // NOVO: parâmetro para o redirect

        // (igual a antes) e-mail vazio
        if (form.getEmail() == null || form.getEmail().isBlank()) {
            model.addAttribute("erro", "E-mail em branco");
            return "recuperar-senha";
        }

        // (igual, só reorganizado) formato do e-mail inválido
        if (!Validador.isEmailValido(form.getEmail())) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        // (igual, só reorganizado) busca o usuário; se não existe, erro
        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());
        if (res.isEmpty()) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        // (igual a antes) tenta enviar. Se o service devolveu texto, deu erro.
        if (passwordResetService.enviarEmailRecuperarSenha(form.getEmail(), res.get()) != null) {
            model.addAttribute("erro", "Ocorreu um erro, tente novamente mais tarde");
            return "recuperar-senha";
        }

        // ALTERADO: antes fazia "return recuperar-senha" direto.
        // Agora guarda a mensagem e REDIRECIONA para o GET.
        // Assim o F5 recarrega só a página e NÃO reenvia o POST (era isso que
        // gerava o código novo).
        redirect.addFlashAttribute("succ",
                "Foi enviado um e-mail com o link, verifique a caixa de mensagens ou spam.");
        return "redirect:/recuperar-senha";
    }

    @GetMapping("/alterar-senha/{token}")
    public String exibirAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model) {
        if (passwordResetService.verificarToken(token) != null) {
            return "redirect:/indefinido";
        }

        model.addAttribute("token", token);
        model.addAttribute("usuarioDTO", form);

        return "alterar-senha";
    }

    @PostMapping("/alterar-senha/{token}")
    public String processarAlterarSenha(@PathVariable String token, @ModelAttribute UsuarioDTO form, Model model) {

        // (igual a antes) token inválido ou expirado: manda para fora
        if (passwordResetService.verificarToken(token) != null) {
            return "redirect:/indefinido";
        }

        // (igual a antes) descobre o e-mail pelo token e tenta trocar a senha
        form.setEmail(passwordResetService.retornarUsuario(token).getEmail());
        String res = usuarioService.alterarSenha(form);

        // se deu erro na troca (ex.: senha fraca), volta para a página
        if (res != null) {
            model.addAttribute("erro", res);
            model.addAttribute("usuarioDTO", form);
            model.addAttribute("token", token);   // NOVO: o HTML provavelmente precisa do token no formulário
            return "alterar-senha";
        }

        // NOVO: senha trocada com sucesso, então apaga o token.
        // O mesmo link não funciona mais.
        passwordResetService.invalidarToken(token);

        model.addAttribute("succ", "Senha alterada com sucesso!");
        return "alterar-senha";
    }
}
