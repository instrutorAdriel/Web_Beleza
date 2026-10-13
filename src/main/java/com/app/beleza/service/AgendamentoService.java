package com.app.beleza.service;

import com.app.beleza.model.dto.AgendamentoDTO;
import com.app.beleza.respository.AgendamentoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgendamentoService {
    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "agendamentosUsuario", key = "#idUsuario + '-' + #pagina + '-' + #tamanho")
    public Page<AgendamentoDTO> listarAgendamentosPorUsuarioPaginado(Integer idModelo, Integer idUsuario, int pagina, int tamanho) {

        // Regra de Proteção: Aplica um delay de 300ms para amortecer requisições em rajada/spam
        aplicarDelayProtecao(300);

        // Cria a paginação ordenada pelos agendamentos mais recentes primeiro
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        return agendamentoRepository.findByModeloOrUsuarioPaginado(idModelo, idUsuario, pageable)
                .map(AgendamentoDTO::new);
    }

    private void aplicarDelayProtecao(long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}