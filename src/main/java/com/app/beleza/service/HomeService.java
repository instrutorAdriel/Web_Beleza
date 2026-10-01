package com.app.beleza.service;

import com.app.beleza.model.*;
import com.app.beleza.model.dto.DepoimentoDTO;
import com.app.beleza.model.Disponibilidade;
import com.app.beleza.model.dto.ServicoDisponibilidadeDTO;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.DisponibilidadeRepository;
import com.app.beleza.respository.HomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HomeService {

    @Autowired
    private HomeRepository homeRepository;

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    public List<ServicoDisponibilidadeDTO> listarServicos() {

        List<ServicoDisponibilidadeDTO> lista = new ArrayList<>();

        List<Disponibilidade> servicos = disponibilidadeRepository.findAll();

        for (Disponibilidade servico : servicos) {

            ServicoDisponibilidadeDTO dto = new ServicoDisponibilidadeDTO();

            dto.setServicoId(servico.getId());
            dto.setNomeServico(servico.getProdutoUnidade().getProduto().getNomeProduto());
            dto.setDescricao(servico.getProdutoUnidade().getProduto().getDescricao());
            dto.setImagem(servico.getProdutoUnidade().getProduto().getImagemAnexo());
            dto.setUnidade(servico.getProdutoUnidade().getUnidade().getNomeUnidade());
            dto.setHora_inicio(servico.getHoraInicio().toString());
            dto.setHora_fim(servico.getHoraFim().toString());

            lista.add(dto);
        }

        return lista;
    }

    /*
    @Autowired
    private DepoimentoRepository depoimentoRepository;

    public List<DepoimentoDTO> listarDepoimentos() {

        List<DepoimentoDTO> lista = new ArrayList<>();

        List<Depoimento> depoimentos = depoimentoRepository.findAll();

        for (Depoimento dep : depoimentos) {
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
    */
}