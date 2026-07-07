package com.app.beleza.service;

import com.app.beleza.model.Usuario;
import com.app.beleza.model.UsuarioDTO;
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

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String cadastrar(UsuarioDTO form) {
        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return "As senhas não conferem.";
        }

        // Uso do e-mail normal sem alterações
        if (usuarioRepository.existsByEmail(form.getEmail())) {
            return "E-mail já cadastrado.";
        }

        String senhaCriptografada = encoder.encode(form.getSenha());

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNomeCompleto(form.getNomeCompleto());
        novoUsuario.setEmail(form.getEmail()); // E-mail normal
        novoUsuario.setDataNascimento(form.getDataNascimento());
        novoUsuario.setEndereco(form.getEndereco());
        novoUsuario.setTelefone(form.getTelefone());
        novoUsuario.setSenha(senhaCriptografada);

        usuarioRepository.save(novoUsuario);
        return null;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }

        Optional<Usuario> resultado = usuarioRepository.findByEmail(email);
        IO.println(resultado);

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
        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());
        if (res.isEmpty()) return "Esse conta não existe.";

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
        if (!Validador.isDataNascimentoValido(form.getDataNascimento())) {
            return "Data de nascimento inválido!";
        }

        Optional<Usuario> resultado = usuarioRepository.findByEmail(form.getEmail());

        if (resultado.isEmpty()) {
            return "E-mail não encontrado.";
        }

        Usuario usuario = resultado.get();
        usuario.setDataNascimento(form.getDataNascimento());
        usuario.setEndereco(form.getEndereco());
        usuario.setTelefone(form.getTelefone());

        return null;
    }

    public UsuarioDTO converterModelParaDTO(Usuario usuario) {
        UsuarioDTO usuarioDTO = new UsuarioDTO();
        usuarioDTO.setNomeCompleto(usuario.getNomeCompleto());
        usuarioDTO.setDataNascimento(usuario.getDataNascimento());
        usuarioDTO.setEmail(usuario.getEmail());
        usuarioDTO.setEndereco(usuario.getEndereco());
        usuarioDTO.setTelefone(usuario.getTelefone());

        return usuarioDTO;
    }
}