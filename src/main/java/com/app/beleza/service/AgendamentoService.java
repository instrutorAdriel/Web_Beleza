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

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class AgendamentoService {
    private static final ZoneId FUSO_BRASILIA = ZoneId.of("America/Sao_Paulo");

    private final AgendamentoRepository agendamentoRepository;

    public AgendamentoService(AgendamentoRepository agendamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
    }

    // A data de hoje faz parte da chave do cache: assim, quando o dia vira,
    // a lista é recalculada e os agendamentos que passaram deixam de aparecer.
    @Transactional(readOnly = true)
    @Cacheable(value = "agendamentosUsuario",
            key = "#idUsuario + '-' + #pagina + '-' + #tamanho + '-' + T(java.time.LocalDate).now(T(java.time.ZoneId).of('America/Sao_Paulo'))")
    public Page<AgendamentoDTO> listarAgendamentosPorUsuarioPaginado(Integer idModelo, Integer idUsuario, int pagina, int tamanho) {

        // Regra de Proteção: Aplica um delay de 300ms para amortecer requisições em rajada/spam
        aplicarDelayProtecao(300);

        // Somente de hoje em diante (compara pela data, não pela hora)
        LocalDate hoje = LocalDate.now(FUSO_BRASILIA);

        // Cria a paginação ordenada pelos agendamentos mais recentes primeiro
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        return agendamentoRepository.findByModeloOrUsuarioPaginado(idModelo, idUsuario, hoje, pageable)
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