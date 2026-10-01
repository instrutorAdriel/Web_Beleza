package com.app.beleza.service;

import com.app.beleza.model.*;
import com.app.beleza.model.DepoimentoDTO;
import com.app.beleza.model.Unidade;
import com.app.beleza.model.AgendamentoDTO;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.ProdutoUnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HomeService {

    @Autowired
    private ProdutoUnidadeRepository produtoUnidadeRepository;



    public List<AgendamentoDTO> listarServicos() {

        List<AgendamentoDTO> lista = new ArrayList<>();

        for (ProdutoUnidade pu : produtoUnidadeRepository.findAll()) {

            Produto produto = pu.getProduto();
            Unidade unidade = pu.getUnidade();

            AgendamentoDTO dto = new AgendamentoDTO();
            dto.setServicoId(pu.getId());                    // id do produto_unidade
            dto.setNomeServico(produto.getNome());
            dto.setDescricao(produto.getDescricao());
            dto.setImagem(produto.getImagem());
            dto.setUnidade(unidade.getNome());

            lista.add(dto);
        }

        return lista;
    }
    @Autowired
    private DepoimentoRepository depoimentoRepository;

    public List<DepoimentoDTO> listarDepoimentos() {

        List<DepoimentoDTO> lista = new ArrayList<>();

        for (Depoimento dep : depoimentoRepository.findAll()) {
            DepoimentoDTO dto = new DepoimentoDTO();
            dto.setNome(dep.getNome());
            dto.setServico(dep.getServico());
            dto.setUnidade(dep.getUnidade());
            dto.setTexto(dep.getTexto());
            dto.setImagem1(dep.getImagem1());
            dto.setImagem2(dep.getImagem2());
            lista.add(dto);
        }
        return lista;
    }
}