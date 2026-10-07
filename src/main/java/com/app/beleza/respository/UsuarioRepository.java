package com.app.beleza.respository;

import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(Integer id);

    Boolean existsByEmail(String email);
}