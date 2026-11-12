package com.app.beleza.service;

import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import com.app.beleza.model.dto.UsuarioDTO;
import com.app.beleza.respository.ModeloRepository;
import com.app.beleza.respository.UsuarioRepository;
import com.app.beleza.utils.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.time.LocalDate;
import java.time.Period;

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String cadastrar(UsuarioDTO form) {
        String erroIdade = validarIdadeMinima(form.getDataNascimento());
        if (erroIdade != null) return erroIdade;

        if (form.getEmail() == null || !Validador.isEmailPessoalComDominioValido(form.getEmail())) {
            return "Informe um e-mail pessoal válido (Gmail, Hotmail, Outlook e outros provedores pessoais).";
        }

        if (form.getSenha() == null || form.getSenha().length() > 25) {
            return "A senha deve ter no máximo 25 caracteres.";
        }

        // Validação de Emojis nos campos do cadastro
        if (Validador.contemEmoji(form.getNomeCompleto()) ||
                Validador.contemEmoji(form.getEmail()) ||
                Validador.contemEmoji(form.getSenha())) {
            return "Os campos do formulário não podem conter emojis.";
        }

        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return "As senhas não conferem.";
        }

        // Uso do e-mail normal sem alterações
        if (usuarioRepository.existsByEmail(form.getEmail())) {
            return "E-mail já cadastrado.";
        }

        String senhaCriptografada = encoder.encode(form.getSenha());

        Usuario dadosUsuario = new Usuario();
        Modelo novoUsuario = new Modelo();
        dadosUsuario.setNomeUsuario(form.getNomeCompleto());
        dadosUsuario.setEmail(form.getEmail());
        dadosUsuario.setSenha(senhaCriptografada);

        novoUsuario.setDataNascimento(form.getDataNascimento());
        novoUsuario.setTelefone(form.getTelefone());
        novoUsuario.setUsuario(dadosUsuario);

        usuarioRepository.save(dadosUsuario);
        modeloRepository.save(novoUsuario);

        return null;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }

        Optional<Usuario> resultado = usuarioRepository.findByEmail(email);

        if (resultado.isEmpty()) {
            System.out.println("[AVISO] O e-mail '" + email + "' não foi encontrado na tabela 'usuarios'.");
            return null;
        }

        Usuario usuario = resultado.get();

        if (!encoder.matches(senha, usuario.getSenha())) {
            System.out.println("[AVISO] O usuário '" + email + "' existe, mas a senha digitada está incorreta.");
            return null;
        }

        System.out.println("[SUCESSO] Usuário '" + email + "' autenticado com sucesso!");
        return usuario;
    }

    public String alterarSenha(UsuarioDTO form) {
        if (form.getSenha() == null || form.getSenha().length() > 25) {
            return "A senha deve ter no máximo 25 caracteres.";
        }

        if (Validador.contemEmoji(form.getSenha())) {
            return "A nova senha não pode conter emojis.";
        }

        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return "As senhas não conferem.";
        }

        Optional<Usuario> resultado = usuarioRepository.findByEmail(form.getEmail());

        if (resultado.isEmpty()) {
            return "E-mail não encontrado.";
        }

        Usuario usuario = resultado.get();
        usuario.setSenha(encoder.encode(form.getSenha()));
        usuarioRepository.save(usuario);
        return null;
    }

    public String atualizarSenha(UsuarioDTO form) {
        if (form.getNovaSenha() == null || form.getNovaSenha().length() > 25) {
            return "A senha deve ter no máximo 25 caracteres.";
        }

        if (Validador.contemEmoji(form.getNovaSenha())) {
            return "A nova senha não pode conter emojis.";
        }

        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());
        if (res.isEmpty()) return "Essa conta não existe.";

        if (!encoder.matches(form.getSenha(), res.get().getSenha())) {
            return "A senha atual não está correta.";
        }

        if (!form.getNovaSenha().equals(form.getConfirmacaoSenha())) {
            return "As senhas não conferem.";
        }

        res.get().setSenha(encoder.encode(form.getNovaSenha()));

        return null;
    }

    public String salvarUsuarioInfo(UsuarioDTO form) {
        if (Validador.contemEmoji(form.getNomeCompleto())) {
            return "O nome completo não pode conter emojis.";
        }

        if (form.getDataNascimento() == null) {
            return "Data de nascimento inválida!";
        }

        String erroIdade = validarIdadeMinima(form.getDataNascimento());
        if (erroIdade != null) return erroIdade;

        Optional<Usuario> usuarioBusca = usuarioRepository.findByEmail(form.getEmail());

        if (usuarioBusca.isEmpty()) {
            return "E-mail não encontrado.";
        }

        Usuario usuario = usuarioBusca.get();

        Optional<Modelo> modeloBusca = modeloRepository.findByUsuario(usuario);

        if (modeloBusca.isEmpty()) {
            return "Perfil de usuário não encontrado.";
        }

        Modelo modelo = modeloBusca.get();

        // 1. Atualiza o Nome Completo na entidade Usuario
        if (form.getNomeCompleto() != null && !form.getNomeCompleto().trim().isEmpty()) {
            usuario.setNomeUsuario(form.getNomeCompleto());
            usuarioRepository.save(usuario);
        }

        // 2. Atualiza a Data de Nascimento e o Telefone na entidade Modelo
        modelo.setDataNascimento(form.getDataNascimento());
        modelo.setTelefone(form.getTelefone());
        modeloRepository.save(modelo);

        return null;
    }

    private String validarIdadeMinima(LocalDate dataNascimento) {
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            return "Informe uma data de nascimento válida.";
        }
        if (Period.between(dataNascimento, LocalDate.now()).getYears() < 12) {
            return "Não é permitido criar ou atualizar a conta de menores de 12 anos, conforme as regras do salão.";
        }
        return null;
    }

    // MÉTODO QUE FALTAVA PARA REMOVER O ERRO EM VERMELHO
    public UsuarioDTO converterModelParaDTO(Modelo usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNomeCompleto(usuario.getUsuario().getNomeUsuario());
        usuarioDTO.setDataNascimento(usuario.getDataNascimento());
        usuarioDTO.setEmail(usuario.getUsuario().getEmail());
        usuarioDTO.setTelefone(usuario.getTelefone());

        return usuarioDTO;
    }
}
