package com.example.demo.service;

import com.example.demo.model.Depoimento;
import com.example.demo.model.DepoimentoDTO;
import com.example.demo.model.Home;
import com.example.demo.model.AgendamentoDTO;
import com.example.demo.respository.DepoimentoRepository;
import com.example.demo.respository.HomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.model.Depoimento;
import com.example.demo.model.DepoimentoDTO;
import com.example.demo.respository.DepoimentoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HomeService {

    @Autowired
    private HomeRepository homeRepository;

    public List<AgendamentoDTO> listarServicos() {

        List<AgendamentoDTO> lista = new ArrayList<>();

        List<Home> servicos = homeRepository.findAll();

        for (Home servico : servicos) {

            AgendamentoDTO dto = new AgendamentoDTO();

            dto.setNomeServico(servico.getNomeServico());
            dto.setDescricao(servico.getDescricao());
            dto.setImagem(servico.getImagem());
            dto.setUnidade(servico.getUnidade());
            dto.setDuracao(servico.getDuracao());

            lista.add(dto);
        }

        return lista;
    }
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
}