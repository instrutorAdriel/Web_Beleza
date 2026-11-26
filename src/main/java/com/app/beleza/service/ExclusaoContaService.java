package com.app.beleza.service;

import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.ModeloRepository;
import com.app.beleza.respository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExclusaoContaService {

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public boolean excluir(Usuario usuario, Modelo modelo) {
        if (modelo != null && agendamentoRepository.existsByModeloIdModeloAndUsuarioIdNot(
                modelo.getIdModelo(), usuario.getId())) {
            return false;
        }

        depoimentoRepository.deleteByUsuarioId(usuario.getId());
        agendamentoRepository.deleteByUsuarioId(usuario.getId());
        if (modelo != null) {
            agendamentoRepository.deleteByModeloIdModelo(modelo.getIdModelo());
            modeloRepository.delete(modelo);
        }
        passwordResetService.removerTokensDoUsuario(usuario);
        usuarioRepository.delete(usuario);
        return true;
    }
}
