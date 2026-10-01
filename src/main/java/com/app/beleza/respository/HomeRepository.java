package com.app.beleza.respository;

import com.app.beleza.model.Disponibilidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeRepository extends JpaRepository<Disponibilidade, Integer> {
}