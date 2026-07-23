package com.app.beleza.respository;

import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

<<<<<<< HEAD
=======
    Optional<Usuario> findById(Long id);

>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
    Boolean existsByEmail(String email);
}
