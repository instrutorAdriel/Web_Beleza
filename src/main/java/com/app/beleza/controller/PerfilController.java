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
                               @RequestParam(required = false, defaultValue = "TODOS") String filtro,
                               Model model,
                               HttpSession session) {

        if (pagina < 0) {
            pagina = 0;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return "redirect:/login";
        }

        agendamentoService.confirmarAgendamentosPendentes(usuario);

        Optional<Modelo> resultado = modeloRepository.findByUsuario(usuario);

        if (resultado.isEmpty()) {
            return "redirect:/";
        }

        // Procura os dados atualizados do banco de dados
        UsuarioDTO usuarioAtualizado = usuarioService.converterModelParaDTO(resultado.get());

        model.addAttribute("tituloPagina", "Bem-vindo " + usuarioAtualizado.getNomeCompleto());

        // Evita conflitos de variáveis e garante que a tela exiba os dados corretos
        if (!model.containsAttribute("usuarioDTO")) {
            model.addAttribute("usuarioDTO", usuarioAtualizado);
        }

        model.addAttribute("abaAtiva", aba);

        if ("agendamento".equals(aba)) {
            Page<AgendamentoDTO> paginaAgendamentos = agendamentoService
                    .listarAgendamentosPorUsuarioPaginado(resultado.get().getIdModelo(), usuario.getId(), pagina, tamanho, filtro);

            model.addAttribute("paginaAgendamentos", paginaAgendamentos);
            model.addAttribute("filtroAtivo", filtro);
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

        // Garante que o e-mail do utilizador na sessão é passado para a procura do Service
        form.setEmail(usuario.getEmail());

        String res = usuarioService.salvarUsuarioInfo(form);
        if (res != null) {
            redirectAttributes.addFlashAttribute("mensagemError", res);
            return "redirect:/perfil?aba=informacao";
        }

        // Atualiza os dados da sessão com a entidade persistida no banco
        Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());
        if (resultado.isPresent()) {
            session.setAttribute("usuarioLogado", resultado.get());
        }

        redirectAttributes.addFlashAttribute("mensagemSucesso", "Informações atualizadas com sucesso!");

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

        if (novaSenha.length() > 25 || confirmacaoSenha.length() > 25) {
            redirectAttributes.addFlashAttribute("mensagemError", "A senha deve ter no máximo 25 caracteres.");
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
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {

        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
        if (usuario == null) return "redirect:/login";

        Optional<Modelo> modeloOpt = modeloRepository.findByUsuario(usuario);
        if (modeloOpt.isEmpty()) return "redirect:/";

        List<Agendamento> agendamentosRealizados = agendamentoRepository
                .findAgendamentosRealizadosSemDepoimento(usuario.getId(), SituacaoAgendamento.REALIZADO);

        if (idAgendamento == null) {
            redirectAttributes.addFlashAttribute("mensagemError", "Por favor, selecione o agendamento correspondente.");
            return "redirect:/perfil?aba=depoimento";
        }

        if (depoimento == null || depoimento.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError", "O depoimento não pode estar em branco.");
            return "redirect:/perfil?aba=depoimento";
        }

        Optional<Agendamento> agendamentoOpt = agendamentoRepository
                .findAgendamentoRealizadoSemDepoimentoPorUsuario(
                        idAgendamento, usuario.getId(), SituacaoAgendamento.REALIZADO);
        if (agendamentoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagemError",
                    "O serviço selecionado não está disponível para avaliação ou já recebeu um depoimento.");
            return "redirect:/perfil?aba=depoimento";
        }

        try {
            String urlImg1 = "";
            String urlImg2 = "";

            if (imagem1 != null && !imagem1.isEmpty()) {
                urlImg1 = cloudinaryService.upload(imagem1, "depoimentos");
            }
            if (imagem2 != null && !imagem2.isEmpty()) {
                urlImg2 = cloudinaryService.upload(imagem2, "depoimentos");
            }

            Depoimento novoDepoimento = new Depoimento();
            novoDepoimento.setDepoimento(depoimento);
            novoDepoimento.setUsuario(usuario);
            novoDepoimento.setModelo(modeloOpt.get());
            novoDepoimento.setAgendamento(agendamentoOpt.get());
            novoDepoimento.setImagemAnexo1(urlImg1);
            novoDepoimento.setImagemAnexo2(urlImg2);

            depoimentoRepository.save(novoDepoimento);

            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Depoimento enviado para análise. Ele será exibido após aprovação da administração.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensagemError", "Erro ao salvar o depoimento: " + e.getMessage());
        }

        return "redirect:/perfil?aba=depoimento";
    }
}
