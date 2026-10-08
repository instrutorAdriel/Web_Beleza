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
    public String processarEmail(@ModelAttribute UsuarioDTO form, Model model) {
        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());

        if (form.getEmail().isBlank()) {
            model.addAttribute("erro", "E-mail em branco");
            return "recuperar-senha";
        } else if (!Validador.isEmailValido(form.getEmail())) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        } else if (res.isEmpty()) {
            model.addAttribute("erro", "E-mail inválido");
            return "recuperar-senha";
        }

        Usuario usuario = res.get();

        if (passwordResetService.enviarEmailRecuperarSenha(form.getEmail(), usuario) == null) {
            model.addAttribute("succ", "Foi enviado um e-mail com o link, verifique a caixa de mensagens ou spam.");
        } else {
            model.addAttribute("erro", "Ocorreu um erro, tente novamente mais tarde");
            return "recuperar-senha";
        }

        return "recuperar-senha";
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
        if (passwordResetService.verificarToken(token) != null) {
            return "redirect:/indefinido";
        }

        form.setEmail(passwordResetService.retornarUsuario(token).getEmail());
        String res = usuarioService.alterarSenha(form);

        if (res != null) {
            model.addAttribute("erro", res);
            model.addAttribute("usuarioDTO", form);
            return "alterar-senha";
        }

        model.addAttribute("succ", "Senha alterada com sucesso!");

        return "alterar-senha";
    }

    /* ─── SEÇÃO PERFIL (GET) ────────────────────────────────────────────── */
    @GetMapping("/perfil")
    public String exibirPerfil(@RequestParam(required = false, defaultValue = "informacao") String aba,
                               @ModelAttribute("usuarioDTO") UsuarioDTO form,
                               Model model,
                               HttpSession session) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/login";
        }

        Optional<Modelo> resultado = modeloRepository.findByUsuario(usuario);

        if (resultado.isEmpty()) {
            return "redirect:/";
        }

        UsuarioDTO usuarioAtualizado = usuarioService.converterModelParaDTO(resultado.get());

        model.addAttribute("tituloPagina", "Bem-vindo " + usuarioAtualizado.getNomeCompleto());
        model.addAttribute("usuarioDTO", usuarioAtualizado);
        model.addAttribute("abaAtiva", aba);

        if ("depoimento".equals(aba)) {
            List<Agendamento> agendamentosRealizados = agendamentoRepository
                    .findAgendamentosRealizadosPorUsuario(usuario.getId(), SituacaoAgendamento.REALIZADO);

            model.addAttribute("listaAgendamentosRealizados", agendamentosRealizados);
        }

        return "perfil";
    }

    /* ─── ATUALIZAR INFORMAÇÕES DO PERFIL ─────────────────────────────────── */
    @PostMapping("/perfil/atualizar-perfil")
    public String atualizarPerfil(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes) {
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

    /* ─── ATUALIZAR SENHA ─────────────────────────────────────────────────── */
    @PostMapping("/perfil/atualizar-senha")
    public String atualizarSenha(@ModelAttribute UsuarioDTO form, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        String senhaAtual = form.getSenha();
        String novaSenha = form.getNovaSenha();
        String confirmacaoSenha = form.getConfirmacaoSenha();

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

        if (!novaSenha.equals(confirmacaoSenha)) {
            redirectAttributes.addFlashAttribute("mensagemError", "A nova senha e a confirmar nova senha não bate.");
            return "redirect:/perfil?aba=configuracao";
        }

        if (!novaSenha.matches(".*[A-Z].*") ||
                !novaSenha.matches(".*[a-z].*") ||
                !novaSenha.matches(".*[0-9].*") ||
                !novaSenha.matches(".*[!@#$%^&*(),.?\":{}|<>" + "_\\-+=\\[\\]\\\\/;'`~].*")) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha nova deve conter ao menos uma letra maiúscula, uma minúscula, um número e um caractere especial. (ex: @, #, !, $).");
            return "redirect:/perfil?aba=configuracao";
        }

        form.setEmail(usuario.getEmail());
        form.setSenha(novaSenha);
        form.setNovaSenha(novaSenha);

        String res = usuarioService.alterarSenha(form);

        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil?aba=configuracao";
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Senha alterada com sucesso!");
        return "redirect:/perfil?aba=configuracao";
    }

    /* ─── PROCESSAR DEPOIMENTO (COM LIMITE DE 2 IMAGENS) ───────────────────── */
    @PostMapping("/perfil/enviar-depoimento")
    public String processarDepoimento(@RequestParam(value = "idAgendamento", required = false) Integer idAgendamento,
                                      @RequestParam(value = "depoimento", required = false) String depoimento,
                                      @RequestParam(value = "imagens", required = false) List<MultipartFile> imagens,
                                      @RequestParam(value = "acao", defaultValue = "salvar") String acao,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        Optional<Modelo> modeloOpt = modeloRepository.findByUsuario(usuario);
        if (modeloOpt.isEmpty()) return "redirect:/";

        List<Agendamento> agendamentosRealizados = agendamentoRepository
                .findAgendamentosRealizadosPorUsuario(usuario.getId(), SituacaoAgendamento.REALIZADO);

        // Remove arquivos vazios da lista enviada
        List<MultipartFile> imagensValidas = new ArrayList<>();
        if (imagens != null) {
            for (MultipartFile img : imagens) {
                if (img != null && !img.isEmpty()) {
                    imagensValidas.add(img);
                }
            }
        }

        // Validação estrita do limite de 2 imagens
        if (imagensValidas.size() > 2) {
            model.addAttribute("mensagemError", "Você pode enviar no máximo 2 imagens por depoimento.");
            model.addAttribute("abaAtiva", "depoimento");
            model.addAttribute("agendamentoIdSelecionado", idAgendamento);
            model.addAttribute("comentarioTexto", depoimento);
            model.addAttribute("listaAgendamentosRealizados", agendamentosRealizados);
            model.addAttribute("usuarioDTO", usuarioService.converterModelParaDTO(modeloOpt.get()));
            return "perfil";
        }

        // ─── 1. AÇÃO DE PRÉ-VISUALIZAÇÃO (PREVIEW) ───────────────────────────────
        if ("preview".equals(acao)) {
            if (!imagensValidas.isEmpty()) {
                try {
                    List<byte[]> listaBytes = new ArrayList<>();
                    List<String> listaBase64 = new ArrayList<>();
                    List<String> listaTipos = new ArrayList<>();

                    for (MultipartFile img : imagensValidas) {
                        byte[] bytes = img.getBytes();
                        listaBytes.add(bytes);
                        listaBase64.add(Base64.getEncoder().encodeToString(bytes));
                        listaTipos.add(img.getContentType() != null ? img.getContentType() : "image/jpeg");
                    }

                    // Armazena na sessão para persistir no reload do form
                    session.setAttribute("tempImagensBytes", listaBytes);
                    session.setAttribute("tempImagensPreviewBase64", listaBase64);
                    session.setAttribute("tempImagensTipos", listaTipos);

                    model.addAttribute("listaPreviewBase64", listaBase64);
                    model.addAttribute("listaPreviewTipos", listaTipos);
                } catch (Exception e) {
                    model.addAttribute("mensagemError", "Erro ao processar imagens para visualização.");
                }
            } else {
                // Recupera da sessão se já tiver sido pré-carregado antes
                List<String> tempPreview = (List<String>) session.getAttribute("tempImagensPreviewBase64");
                List<String> tempTipos = (List<String>) session.getAttribute("tempImagensTipos");

                if (tempPreview != null) {
                    model.addAttribute("listaPreviewBase64", tempPreview);
                    model.addAttribute("listaPreviewTipos", tempTipos);
                }
            }

            model.addAttribute("abaAtiva", "depoimento");
            model.addAttribute("agendamentoIdSelecionado", idAgendamento);
            model.addAttribute("comentarioTexto", depoimento);
            model.addAttribute("listaAgendamentosRealizados", agendamentosRealizados);
            model.addAttribute("usuarioDTO", usuarioService.converterModelParaDTO(modeloOpt.get()));

            return "perfil";
        }

        // ─── 2. AÇÃO DE SALVAR O DEPOIMENTO ─────────────────────────────────────
        if (idAgendamento == null) {
            redirectAttributes.addFlashAttribute("mensagemError", "Por favor, selecione o agendamento correspondente.");
            return "redirect:/perfil?aba=depoimento";
        }

        if (depoimento == null || depoimento.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "O depoimento não pode estar em branco.");
            return "redirect:/perfil?aba=depoimento";
        }

        try {
            Depoimento novoDepoimento = new Depoimento();
            novoDepoimento.setDepoimento(depoimento);
            novoDepoimento.setUsuario(usuario);
            novoDepoimento.setModelo(modeloOpt.get());
            novoDepoimento.setAvaliacao(5);

            agendamentoRepository.findById(idAgendamento).ifPresent(novoDepoimento::setAgendamento);

            List<byte[]> bytesParaUpload = new ArrayList<>();

            // Se vieram arquivos novos na submissão, usa eles; caso contrário, usa os salvos na sessão
            if (!imagensValidas.isEmpty()) {
                for (MultipartFile f : imagensValidas) {
                    bytesParaUpload.add(f.getBytes());
                }
            } else if (session.getAttribute("tempImagensBytes") != null) {
                bytesParaUpload = (List<byte[]>) session.getAttribute("tempImagensBytes");
            }

            // Realiza upload de até 2 imagens para o Cloudinary e associa ao depoimento
            String urlImg1 = "";
            String urlImg2 = "";

            if (bytesParaUpload != null && !bytesParaUpload.isEmpty()) {
                if (bytesParaUpload.size() > 0) {
                    urlImg1 = cloudinaryService.upload(bytesParaUpload.get(0), "depoimento");
                }
                if (bytesParaUpload.size() > 1) {
                    urlImg2 = cloudinaryService.upload(bytesParaUpload.get(1), "depoimento");
                }
            }

            novoDepoimento.setImagemAnexo1(urlImg1);
            novoDepoimento.setImagemAnexo2(urlImg2);

            depoimentoRepository.save(novoDepoimento);

            // Limpa as imagens temporárias da sessão
            session.removeAttribute("tempImagensBytes");
            session.removeAttribute("tempImagensPreviewBase64");
            session.removeAttribute("tempImagensTipos");

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Depoimento enviado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemError", "Erro ao salvar depoimento: " + e.getMessage());
        }

        return "redirect:/perfil?aba=depoimento";
    }
}