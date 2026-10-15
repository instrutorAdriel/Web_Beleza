package com.app.beleza.controller;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import com.app.beleza.model.dto.AgendamentoDTO;
import com.app.beleza.model.dto.UsuarioDTO;
import com.app.beleza.model.enums.SituacaoAgendamento;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.ModeloRepository;
import com.app.beleza.respository.UsuarioRepository;
import com.app.beleza.service.AgendamentoService;
import com.app.beleza.service.CloudinaryService;
import com.app.beleza.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("/perfil")
public class PerfilController {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AgendamentoService agendamentoService;

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @GetMapping
    public String exibirPerfil(@RequestParam(required = false, defaultValue = "informacao") String aba,
                               @RequestParam(defaultValue = "0") int pagina,
                               @RequestParam(defaultValue = "5") int tamanho,
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

        if ("agendamento".equals(aba)) {
            Page<AgendamentoDTO> paginaAgendamentos = agendamentoService
                    .listarAgendamentosPorUsuarioPaginado(resultado.get().getIdModelo(), usuario.getId(), pagina, tamanho);

            model.addAttribute("paginaAgendamentos", paginaAgendamentos);
        }

        if ("depoimento".equals(aba)) {
            List<Agendamento> agendamentosRealizados = agendamentoRepository
                    .findAgendamentosRealizadosPorUsuarioESituacao(usuario.getId(), SituacaoAgendamento.REALIZADO);

            model.addAttribute("listaAgendamentosRealizados", agendamentosRealizados);
        }

        return "perfil";
    }

    /* ─── ATUALIZAR INFORMAÇÕES DO PERFIL ─────────────────────────────────── */
    @PostMapping("/atualizar-perfil")
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

    @PostMapping("/atualizar-senha")
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

    @PostMapping("/enviar-depoimento")
    public String processarDepoimento(@RequestParam(value = "idAgendamento", required = false) Integer idAgendamento,
                                      @RequestParam(value = "depoimento", required = false) String depoimento,
                                      @RequestParam(value = "imagem1", required = false) MultipartFile imagem1,
                                      @RequestParam(value = "imagem2", required = false) MultipartFile imagem2,
                                      @RequestParam(value = "acao", defaultValue = "salvar") String acao,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        Optional<Modelo> modeloOpt = modeloRepository.findByUsuario(usuario);
        if (modeloOpt.isEmpty()) return "redirect:/";

        List<Agendamento> agendamentosRealizados = agendamentoRepository
                .findAgendamentosRealizadosSemDepoimento(usuario.getId(), SituacaoAgendamento.REALIZADO);

        try {
            if (imagem1 != null && !imagem1.isEmpty()) {
                byte[] bytes1 = imagem1.getBytes();
                session.setAttribute("tempImg1Bytes", bytes1);
                session.setAttribute("tempImg1Base64", Base64.getEncoder().encodeToString(bytes1));
                session.setAttribute("tempImg1Tipo", imagem1.getContentType());
            }

            if (imagem2 != null && !imagem2.isEmpty()) {
                byte[] bytes2 = imagem2.getBytes();
                session.setAttribute("tempImg2Bytes", bytes2);
                session.setAttribute("tempImg2Base64", Base64.getEncoder().encodeToString(bytes2));
                session.setAttribute("tempImg2Tipo", imagem2.getContentType());
            }
        } catch (Exception e) {
            model.addAttribute("mensagemError", "Erro ao processar imagens para pré-visualização.");
        }

        String img1Base64 = (String) session.getAttribute("tempImg1Base64");
        String img1Tipo = (String) session.getAttribute("tempImg1Tipo");
        String img2Base64 = (String) session.getAttribute("tempImg2Base64");
        String img2Tipo = (String) session.getAttribute("tempImg2Tipo");

        model.addAttribute("imagem1PreviewBase64", img1Base64);
        model.addAttribute("imagem1Tipo", img1Tipo);
        model.addAttribute("imagem2PreviewBase64", img2Base64);
        model.addAttribute("imagem2Tipo", img2Tipo);

        if("preview".equals(acao)) {
            model.addAttribute("abaAtiva", "depoimento");
            model.addAttribute("agendamentoIdSelecionado", idAgendamento);
            model.addAttribute("comentarioTexto", depoimento);
            model.addAttribute("listaAgendamentosRealizados", agendamentosRealizados);
            model.addAttribute("usuarioDTO", usuarioService.converterModelParaDTO(modeloOpt.get()));
            return "perfil";
        }

        if (idAgendamento == null) {
            redirectAttributes.addFlashAttribute("mensagemError", "Por favor, selecione o agendamento correspondente.");
            return "redirect:/perfil?aba=depoimento";
        }

        if (depoimento == null || depoimento.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "O depoimento não pode estar em branco.");
            return "redirect:/perfil?aba=depoimento";
        }

        Optional<Agendamento> agendamentoOpt = agendamentoRepository.findById(idAgendamento);
        if (agendamentoOpt.isEmpty() || agendamentoOpt.get().getSituacao() != SituacaoAgendamento.REALIZADO) {
            redirectAttributes.addFlashAttribute("mensagemError", "O depoimento só pode ser registrado para procedimentos já REALIZADOS.");
            return "redirect:/perfil?aba=depoimento";
        }

        if (agendamentoRepository.findByAgendamentoIdAndUsuario(idAgendamento, usuario.getId()).isPresent()) {
            redirectAttributes.addFlashAttribute("mensagemError", "Você já enviou um depoimento para este agendamento.");
            return "redirect:/perfil?aba=depoimento";
        }

        try {
            byte[] bytesImg1 = (imagem1 != null && !imagem1.isEmpty()) ? imagem1.getBytes() : (byte[]) session.getAttribute("tempImg1Bytes");
            byte[] bytesImg2 = (imagem2 != null && !imagem2.isEmpty()) ? imagem2.getBytes() : (byte[]) session.getAttribute("tempImg2Bytes");

            String urlImg1 = "";
            String urlImg2 = "";

            if (bytesImg1 != null && bytesImg1.length > 0) {
                urlImg1 = cloudinaryService.upload(bytesImg1, "depoimentos");
            }
            if (bytesImg2 != null && bytesImg2.length > 0) {
                urlImg2 = cloudinaryService.upload(bytesImg2, "depoimentos");
            }

            Depoimento novoDepoimento = new Depoimento();
            novoDepoimento.setDepoimento(depoimento);
            novoDepoimento.setUsuario(usuario);
            novoDepoimento.setModelo(modeloOpt.get());
            novoDepoimento.setAgendamento(agendamentoOpt.get());
            novoDepoimento.setImagemAnexo1(urlImg1);
            novoDepoimento.setImagemAnexo2(urlImg2);

            depoimentoRepository.save(novoDepoimento);

            session.removeAttribute("tempImg1Bytes");
            session.removeAttribute("tempImg1Base64");
            session.removeAttribute("tempImg1Tipo");
            session.removeAttribute("tempImg2Bytes");
            session.removeAttribute("tempImg2Base64");
            session.removeAttribute("tempImg2Tipo");

            redirectAttributes.addFlashAttribute("mensagemSucesso", "Depoimento enviado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemError", "Erro ao salvar o depoimento: " + e.getMessage());
        }

        return "redirect:/perfil?aba=depoimento";
    }
}
