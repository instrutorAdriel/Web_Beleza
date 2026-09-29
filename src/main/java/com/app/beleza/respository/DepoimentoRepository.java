package com.app.beleza.respository;

import com.app.beleza.model.Depoimento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepoimentoRepository extends JpaRepository<Depoimento, Long> {
}