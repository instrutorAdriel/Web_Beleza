package com.app.beleza.respository;

import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModeloRepository extends JpaRepository <Modelo, Integer> {

    Optional<Modelo> findByUsuario(Usuario usuario);
}
