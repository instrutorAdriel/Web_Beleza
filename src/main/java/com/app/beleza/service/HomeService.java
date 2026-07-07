package com.app.beleza.service;

import com.app.beleza.model.Home;
import com.app.beleza.model.AgendamentoDTO;
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
}