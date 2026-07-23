package com.app.beleza.service;

<<<<<<< HEAD
=======
import com.app.beleza.model.*;
import com.app.beleza.model.DepoimentoDTO;
import com.app.beleza.model.Home;
import com.app.beleza.model.AgendamentoDTO;
import com.app.beleza.respository.DepoimentoRepository;
import com.app.beleza.respository.HomeRepository;
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
import com.app.beleza.model.Home;
import com.app.beleza.model.AgendamentoDTO;
import com.app.beleza.respository.HomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
<<<<<<< HEAD
=======
import com.app.beleza.model.DepoimentoDTO;
import com.app.beleza.respository.DepoimentoRepository;
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)

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
<<<<<<< HEAD
=======
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
>>>>>>> 6b5bcaa (Correção responsividade plataforma mobile)
}