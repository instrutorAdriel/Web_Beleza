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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

@Service
@Transactional
public class HomeService {
    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private DepoimentoRepository depoimentoRepository;

    @Autowired
    private ProdutoUnidadeRepository produtoUnidadeRepository;

    @Cacheable("homeProcedimentos")
    public List<ServicoDisponibilidadeDTO> listarProcedimentosPorUnidade() {
        List<ServicoDisponibilidadeDTO> lista = new ArrayList<>();
        //Optional<List<Disponibilidade>> optional = disponibilidadeRepository.findServicosUnicosComRelacionamentos();
        Optional<List<ProdutoUnidade>> optional = produtoUnidadeRepository.findProdutoUnidadeComRelacionamentos();
        List<ProdutoUnidade> procedimentos = new ArrayList<>();

        if (optional.isPresent()) {
            procedimentos = optional.get();
        }

        for (ProdutoUnidade procedimento : procedimentos) {
            ServicoDisponibilidadeDTO dto = new ServicoDisponibilidadeDTO();
            dto.setServicoId(procedimento.getId());
            dto.setNomeServico(procedimento.getProduto().getNomeProduto());
            dto.setDescricao(procedimento.getProduto().getDescricao());
            dto.setImagem(procedimento.getProduto().getImagemAnexo());
            dto.setUnidade(procedimento.getUnidade().getNomeUnidade());
            //dto.setHora_fim(procedimento.getHoraFim().toString());
            //dto.setHora_inicio(procedimento.getHoraInicio().toString());
            lista.add(dto);
        }

        return lista;
    }

    public Page<DepoimentoDTO> listarDepoimentosRecentes(String unidade, String servico) {
        // Limite fixo de 15 itens (Página 0, tamanho 15)
        Pageable pageable = PageRequest.of(0, 15);

        String filtroUnidade = (unidade == null || unidade.isEmpty() || unidade.equals("TODAS")) ? null : unidade;
        String filtroServico = (servico == null || servico.isEmpty() || servico.equals("TODOS")) ? null : servico;

        Page<Depoimento> paginaDepoimentos = depoimentoRepository.buscarDepoimentosFiltrados(filtroUnidade, filtroServico, pageable);

        List<DepoimentoDTO> dtosDaPagina = new ArrayList<>();
        for (Depoimento depoimento : paginaDepoimentos.getContent()) {
            DepoimentoDTO dto = new DepoimentoDTO();
            dto.setNome(depoimento.getUsuario().getNomeUsuario());

            if (depoimento.getAgendamento() != null && depoimento.getAgendamento().getDisponibilidade() != null) {
                dto.setServico(depoimento.getAgendamento().getDisponibilidade().getProdutoUnidade().getProduto().getNomeProduto());
                dto.setUnidade(depoimento.getAgendamento().getDisponibilidade().getProdutoUnidade().getUnidade().getNomeUnidade());
                dto.setDataAgendamento(depoimento.getAgendamento().getDataHoraAgendamento());
            }

            dto.setTexto(depoimento.getDepoimento());
            dto.setImagem1(depoimento.getImagemAnexo1());
            dto.setImagem2(depoimento.getImagemAnexo2());

            dtosDaPagina.add(dto);
        }

        return new PageImpl<>(dtosDaPagina, pageable, paginaDepoimentos.getTotalElements());
    }
}