package com.app.beleza.respository;

import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModeloRepository extends JpaRepository<Modelo, Integer> {
    Optional<Modelo> findById(Integer id);

    Optional<Modelo> findByUsuario(Usuario usuario);

    Optional<Modelo> findByTelefone(String telefone);
}