package com.app.beleza.respository;

import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.SessaoUsuario;
import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessaoUsuarioRepository extends JpaRepository<SessaoUsuario, Long> {
    Optional<List<SessaoUsuario>> findByLembreteEnviadoFalse();
    
    Optional<List<Usuario>> findByUsuarios();

    Optional<List<SessaoAtendimento>> findBySessaoAtendimentos();

}
