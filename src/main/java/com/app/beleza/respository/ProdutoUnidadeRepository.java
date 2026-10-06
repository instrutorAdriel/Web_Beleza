package com.app.beleza.respository;

import com.app.beleza.model.ProdutoUnidade;
import com.app.beleza.model.Unidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoUnidadeRepository extends JpaRepository<ProdutoUnidade, Integer> {

}