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

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService {

    private static final int TAMANHO_MAXIMO_NOME = 255;
    private static final int TAMANHO_MAXIMO_EMAIL_ANTES_ARROBA = 64;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String cadastrar(UsuarioDTO form) {
        if (form.getNomeCompleto() == null ||
                form.getNomeCompleto().trim().length() < 3 ||
                !form.getNomeCompleto().trim().contains(" ")) {
            return "Informe seu nome e sobrenome.";
        }

        if (form.getNomeCompleto().trim().length() > TAMANHO_MAXIMO_NOME) {
            return "O nome completo deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.";
        }

        if (Validador.contemEmoji(form.getNomeCompleto()) ||
                !form.getNomeCompleto().matches("^[A-Za-zÀ-ÿ\\s]+$")) {
            return "O nome completo deve conter apenas letras e espaços.";
        }

        if (form.getDataNascimento() == null || form.getDataNascimento().isAfter(LocalDate.now())) {
            return "Informa uma data de nascimento válida.";
        }

        int idade = Period.between(form.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 12) {
            return "A idade m\u00ednima para participar do Sal\u00e3o de Beleza do Senac \u00e9 12 anos.";
        }

        String digitosTelefone = form.getTelefone().replaceAll("\\D", "");
        if (form.getTelefone() == null || digitosTelefone.length() < 10 || digitosTelefone.length() > 11) {
            return "Informa um telefone válido com DDD.";
        }

        if (form.getEmail() != null) {
            String emailInformado = form.getEmail().trim();
            int posicaoArroba = emailInformado.indexOf('@');
            if (posicaoArroba > TAMANHO_MAXIMO_EMAIL_ANTES_ARROBA) {
                return "O e-mail deve ter no máximo " + TAMANHO_MAXIMO_EMAIL_ANTES_ARROBA + " caracteres antes do @.";
            }
        }

        if (form.getEmail() == null || Validador.contemEmoji(form.getEmail()) ||
                !Validador.isEmailPessoalComDominioValido(form.getEmail().trim())) {
            return "Informe um e-mail válido.";
        }

        String emailNormalizado = form.getEmail().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            return "Esse e-mail já esta cadastrado.";
        }

        if (form.getSenha() == null || form.getSenha().length() < 8 || form.getSenha().length() > 25) {
            return "A senha deve ter entre 8 e 25 caracteres.";
        }

        if (Validador.contemEmoji(form.getSenha()) || !isSenhaForte(form.getSenha())) {
            return "A senha deve contre letra maiúsucla, minúscula, número e caractere especial.";
        }

        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return "As senhas não conferem.";
        }

        String senhaCriptografada = encoder.encode(form.getSenha());

        Usuario dadosUsuario = new Usuario();
        dadosUsuario.setNomeUsuario(form.getNomeCompleto().trim());
        dadosUsuario.setEmail(emailNormalizado);
        dadosUsuario.setSenha(senhaCriptografada);

        Modelo novoUsuario = new Modelo();
        novoUsuario.setDataNascimento(form.getDataNascimento());
        novoUsuario.setTelefone(form.getTelefone().trim());
        novoUsuario.setUsuario(dadosUsuario);

        usuarioRepository.save(dadosUsuario);
        modeloRepository.save(novoUsuario);

        return null;
    }

    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }

        Optional<Usuario> resultado = usuarioRepository.findByEmail(email.trim().toLowerCase());

        if (resultado.isEmpty()) {
            System.out.println("[AVISO] O e-mail '" + email + "' não foi encontrado na tabela 'usuarios'.");
            return null;
        }

        Usuario usuario = resultado.get();

        if (usuario.getSituacao() == 'I') {
            return null;
        }

        if (!encoder.matches(senha, usuario.getSenha())) {
            System.out.println("[AVISO] O usuário '" + email + "' existe, mas a senha digitada está incorreta.");
            return null;
        }

        System.out.println("[SUCESSO] Usuário '" + email + "' autenticado com sucesso!");
        return usuario;
    }

    public String alterarSenha(UsuarioDTO form) {
        if (form.getSenha() == null || form.getSenha().length() < 8 || form.getSenha().length() > 25) {
            return "A senha deve ter entre 8 e 25 caracteres.";
        }

        if (Validador.contemEmoji(form.getSenha()) || !isSenhaForte(form.getSenha())) {
            return "A nova senha deve conter letra maiúscula, minúscula, número e caractere especial.";
        }

        if (!form.getSenha().equals(form.getConfirmacaoSenha())) {
            return "A confirmação de senha não confere.";
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
        if (form.getNovaSenha() == null
                || form.getNovaSenha().length() < 8
                || form.getNovaSenha().length() > 25) {
            return "A senha deve ter entre 8 e 25 caracteres.";
        }

        if (Validador.contemEmoji(form.getNovaSenha()) || !isSenhaForte(form.getNovaSenha())) {
            return "A nova senha deve conter letra maiúscula, minúscula, número e caractere especial.";
        }

        Optional<Usuario> res = usuarioRepository.findByEmail(form.getEmail());
        if (res.isEmpty()) return "Essa conta não existe.";

        if (!encoder.matches(form.getSenha(), res.get().getSenha())) {
            return "A senha atual não está correta.";
        }

        if (!form.getNovaSenha().equals(form.getConfirmacaoSenha())) {
            return "A confirmação de senha não confere.";
        }

        res.get().setSenha(encoder.encode(form.getNovaSenha()));

        return null;
    }

    public String salvarUsuarioInfo(UsuarioDTO form) {
        if (form.getNomeCompleto() == null || form.getNomeCompleto().trim().isEmpty()) {
            return "Informe seu nome e sobrenome.";
        }

        String nomeLimpo = form.getNomeCompleto().trim().replaceAll("\\s+", " ");

        if (Validador.contemEmoji(nomeLimpo)) {
            return "O nome completo não pode conter emojis.";
        }

        if (form.getNomeCompleto() != null && form.getNomeCompleto().trim().length() > TAMANHO_MAXIMO_NOME) {
            return "O nome completo deve ter no máximo " + TAMANHO_MAXIMO_NOME + " caracteres.";
        }

        // Exige nome e sobrenome
        if (!nomeLimpo.matches("^[A-Za-zÀ-ÿ]{2,}(\\s+[A-Za-zÀ-ÿ]{2,})+$")) {
            return "Informe nome e sobrenome válidos (mínimo de 2 letras em cada, sem números ou símbolos).";
        }

        if (form.getDataNascimento() == null || form.getDataNascimento().isAfter(LocalDate.now())) {
            return "Data de nascimento inválida!";
        }

        int idade = Period.between(form.getDataNascimento(), LocalDate.now()).getYears();
        if (idade < 12) {
            return "A idade mínima para participar do Salão de Beleza do Senac é 12 anos.";
        }

        // Valida exatamente 15 caracteres (XX) XXXXX-XXXX
        if (form.getTelefone() == null || !form.getTelefone().trim().matches("^\\(\\d{2}\\)\\s\\d{5}-\\d{4}$")) {
            return "Informe um telefone válido com 15 caracteres no formato (XX) XXXXX-XXXX.";
        }

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

        usuario.setNomeUsuario(nomeLimpo);
        usuarioRepository.save(usuario);

        modelo.setDataNascimento(form.getDataNascimento());
        modelo.setTelefone(form.getTelefone().trim());
        modeloRepository.save(modelo);

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

    private boolean isSenhaForte(String senha) {
        return senha.matches(".*[A-Z].*")
                && senha.matches(".*[a-z].*")
                && senha.matches(".*[0-9].*")
                && senha.matches(".*[!@#$%^&*(),.?\":{}|<>_\\-+=\\[\\]\\\\/;'`~].*");
    }

    public String identificarCampoErro(String mensagemErro) {
        String msg = mensagemErro.toLowerCase();
        if (msg.contains("nome")) return "nomeCompleto";
        if (msg.contains("nascimento") || msg.contains("idade") || msg.contains("anos")) return "dataNascimento";
        if (msg.contains("telefone")) return "telefone";
        if (msg.contains("e-mail") || msg.contains("email")) return "email";
        if (msg.contains("coincidem") || msg.contains("confirma")) return "confirmacaoSenha";
        if (msg.contains("senha")) return "senha";
        return null;
    }
}