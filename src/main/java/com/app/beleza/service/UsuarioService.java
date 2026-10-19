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

@Service
@Transactional
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String cadastrar(UsuarioDTO form) {
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
        dadosUsuario.setEmail(form.getEmail()); // E-mail normal
        dadosUsuario.setSenha(senhaCriptografada);

        // Atribuição direta do LocalDate vindo da DTO
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

        // Compara se a senha digitada bate com a criptografada do banco
        if (!encoder.matches(senha, usuario.getSenha())) {
            System.out.println("[AVISO] O usuário '" + email + "' existe, mas a senha digitada está incorreta.");
            return null;
        }

        System.out.println("[SUCESSO] Usuário '" + email + "' autenticado com sucesso!");
        return usuario;
    }

    public String alterarSenha(UsuarioDTO form) {
        // Validação de Emojis na nova senha
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
        // Validação de Emojis na alteração de senha do perfil
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
        // Validação da data de nascimento se preenchida
        if (form.getDataNascimento() == null) {
            return "Data de nascimento inválida!";
        }

        Optional<Usuario> usuarioBusca = usuarioRepository.findByEmail(form.getEmail());

        if (usuarioBusca.isEmpty()) {
            return "E-mail não encontrado.";
        }

        Optional<Modelo> modeloBusca = modeloRepository.findByUsuario(usuarioBusca.get());

        if (modeloBusca.isEmpty()) {
            return "Perfil de usuário não encontrado.";
        }

        Modelo modelo = modeloBusca.get();

        // Atribuição direta do LocalDate vindo da DTO
        modelo.setDataNascimento(form.getDataNascimento());
        modelo.setTelefone(form.getTelefone());

        return null;
    }

    public UsuarioDTO converterModelParaDTO(Modelo usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNomeCompleto(usuario.getUsuario().getNomeUsuario());

        // Atribuição direta do LocalDate para a DTO
        usuarioDTO.setDataNascimento(usuario.getDataNascimento());
        usuarioDTO.setEmail(usuario.getUsuario().getEmail());
        usuarioDTO.setTelefone(usuario.getTelefone());

        return usuarioDTO;
    }
}