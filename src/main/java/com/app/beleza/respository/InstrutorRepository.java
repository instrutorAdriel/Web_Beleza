package com.app.beleza.respository;

import com.app.beleza.model.Instrutor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InstrutorRepository extends JpaRepository<Instrutor, Integer> {
    Optional<Instrutor> findFirstByOrderByIdInstrutorAsc();
}