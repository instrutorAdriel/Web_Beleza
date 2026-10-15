package com.app.beleza.service;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Disponibilidade;
import com.app.beleza.model.ProdutoUnidade;
import com.app.beleza.model.dto.DepoimentoDTO;
import com.app.beleza.model.dto.ServicoDisponibilidadeDTO;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.DisponibilidadeRepository;
import com.app.beleza.respository.ProdutoUnidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class HomeService {

    @Autowired
    private ProdutoUnidadeRepository produtoUnidadeRepository; // 1. Injeta o repositório de ProdutoUnidade

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    public List<ServicoDisponibilidadeDTO> listarServicos() {

        List<ServicoDisponibilidadeDTO> lista = new ArrayList<>();

        // 2. AQUI ENTRA A LÓGICA:
        // Em vez de buscar da tabela 'disponibilidade', buscamos da tabela 'produto_unidade'
        List<ProdutoUnidade> servicosUnidade = produtoUnidadeRepository.findAll();

        for (ProdutoUnidade pu : servicosUnidade) {

            ServicoDisponibilidadeDTO dto = new ServicoDisponibilidadeDTO();

            // Pega as informações do produto e da unidade diretamente
            dto.setServicoId(pu.getProduto().getId());
            dto.setNomeServico(pu.getProduto().getNomeProduto());
            dto.setDescricao(pu.getProduto().getDescricao());
            dto.setImagem(pu.getProduto().getImagemAnexo());
            dto.setUnidade(pu.getUnidade().getNomeUnidade());

            lista.add(dto);
        }

        return lista; // Retorna para a tela (HomeController)
    }


    public List<DepoimentoDTO> listarDepoimentos() {

        List<DepoimentoDTO> lista = new ArrayList<>();

        List<Depoimento> depoimentos = depoimentoRepository.findAll();

        for (Depoimento depoimento : depoimentos) {
            DepoimentoDTO dto = new DepoimentoDTO();
            dto.setNome(depoimento.getUsuario().getNomeUsuario());
            dto.setServico(depoimento.getAgendamento().getDisponibilidade().getProdutoUnidade().getProduto().getNomeProduto());
            dto.setUnidade(depoimento.getAgendamento().getDisponibilidade().getProdutoUnidade().getUnidade().getNomeUnidade());
            dto.setTexto(depoimento.getDepoimento());
            dto.setImagem1(depoimento.getImagemAnexo1());
            dto.setImagem2(depoimento.getImagemAnexo2());
            lista.add(dto);
        }
        return lista;
    }
}