package com.app.beleza.service;

import com.app.beleza.model.Depoimento;
import com.app.beleza.model.Disponibilidade;
import com.app.beleza.model.dto.DepoimentoDTO;
import com.app.beleza.model.dto.ServicoDisponibilidadeDTO;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.DisponibilidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class HomeService {
    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    @Cacheable("homeServicos")
    public List<ServicoDisponibilidadeDTO> listarServicos() {
        List<ServicoDisponibilidadeDTO> lista = new ArrayList<>();
        Optional<List<Disponibilidade>> optional = disponibilidadeRepository.findServicosUnicosComRelacionamentos();
        List<Disponibilidade> servicos = new ArrayList<>();

        if (optional.isPresent()) {
            servicos = optional.get();
        }

        for (Disponibilidade servico : servicos) {
            ServicoDisponibilidadeDTO dto = new ServicoDisponibilidadeDTO();
            dto.setServicoId(servico.getId());
            dto.setNomeServico(servico.getProdutoUnidade().getProduto().getNomeProduto());
            dto.setDescricao(servico.getProdutoUnidade().getProduto().getDescricao());
            dto.setImagem(servico.getProdutoUnidade().getProduto().getImagemAnexo());
            dto.setUnidade(servico.getProdutoUnidade().getUnidade().getNomeUnidade());
            dto.setHora_fim(servico.getHoraFim().toString());
            dto.setHora_inicio(servico.getHoraInicio().toString());
            lista.add(dto);
        }

        return lista;
    }

    @Cacheable("homeDepoimentos")
    public List<DepoimentoDTO> listarDepoimentos() {
        List<DepoimentoDTO> lista = new ArrayList<>();
        Optional<List<Depoimento>> optional = depoimentoRepository.findAllComRelacionamento();
        List<Depoimento> depoimentos = new ArrayList<>();

        if (optional.isPresent()) {
            depoimentos = optional.get();
        }

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