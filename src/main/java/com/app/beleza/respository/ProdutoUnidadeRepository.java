package com.app.beleza.respository;

import com.app.beleza.model.ProdutoUnidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoUnidadeRepository extends JpaRepository<ProdutoUnidade, Integer> {
    @Query("SELECT pu FROM ProdutoUnidade pu " +
            "JOIN FETCH pu.produto p " +
            "JOIN FETCH pu.unidade u")
    Optional<List<ProdutoUnidade>> findProdutoUnidadeComRelacionamentos();
}