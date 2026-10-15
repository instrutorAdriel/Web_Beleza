package com.app.beleza.service;

import com.app.beleza.model.dto.AgendamentoDTO;
import com.app.beleza.respository.AgendamentoRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Disponibilidade;
import com.app.beleza.model.Instrutor;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.Usuario;
import com.app.beleza.model.dto.SessaoAtendimentoDTO;
import com.app.beleza.model.enums.SituacaoAgendamento;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.DisponibilidadeRepository;
import com.app.beleza.respository.InstrutorRepository;
import com.app.beleza.respository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgendamentoService {

    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private DisponibilidadeRepository disponibilidadeRepository;
    @Autowired private ModeloRepository modeloRepository;
    @Autowired private InstrutorRepository instrutorRepository;

    /** Lista a disponibilidade e calcula se o usuário logado já agendou. */
    @Transactional(readOnly = true)
    public List<SessaoAtendimentoDTO> listarPorDisponibilidade(Integer idDisponibilidade, Usuario usuarioLogado) {
        List<SessaoAtendimentoDTO> lista = new ArrayList<>();

        disponibilidadeRepository.findById(idDisponibilidade).ifPresent(d -> {
            boolean jaAgendou = false;
            if (usuarioLogado != null) {
                jaAgendou = agendamentoRepository.existsByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
                        usuarioLogado.getId(), idDisponibilidade, SituacaoAgendamento.CANCELADO);
            }

            SessaoAtendimentoDTO dto = new SessaoAtendimentoDTO(
                    d.getId().longValue(),
                    d.getDataDisponibilidade() != null
                            ? d.getDataDisponibilidade().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "",
                    d.getHoraInicio() != null
                            ? d.getHoraInicio().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"))
                            : "",
                    d.getTotalVagas() != null ? d.getTotalVagas() : 0,
                    jaAgendou
            );
            lista.add(dto);
        });

        return lista;
    }

    @Transactional
    public void agendar(Integer idDisponibilidade, Usuario usuarioLogado) {
        if (usuarioLogado == null) throw new RuntimeException("Usuário não autenticado.");

        Modelo modelo = modeloRepository.findByUsuario(usuarioLogado)
                .orElseThrow(() -> new RuntimeException("Complete seu perfil antes de agendar."));

        Disponibilidade disp = disponibilidadeRepository.findByIdComLock(idDisponibilidade)
                .orElseThrow(() -> new RuntimeException("Horário não encontrado."));

        if (disp.getTotalVagas() == null || disp.getTotalVagas() <= 0) {
            throw new RuntimeException("Esse horário está esgotado.");
        }

        boolean jaTem = agendamentoRepository.existsByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
                usuarioLogado.getId(), idDisponibilidade, SituacaoAgendamento.CANCELADO);
        if (jaTem) {
            throw new RuntimeException("Você já agendou esse horário.");
        }
        Instrutor instrutor = instrutorRepository.findFirstByOrderByIdInstrutorAsc()
                .orElseThrow(() -> new RuntimeException("Nenhum instrutor cadastrado no sistema."));

        Agendamento ag = new Agendamento();
        ag.setUsuario(usuarioLogado);
        ag.setModelo(modelo);
        ag.setInstrutor(instrutor);
        ag.setDisponibilidade(disp);
        ag.setSituacao(SituacaoAgendamento.CONFIRMADO);
        ag.setObservacao("");
        ag.setDataHoraAgendamento(LocalDateTime.of(disp.getDataDisponibilidade(), disp.getHoraInicio()));

        agendamentoRepository.save(ag);

        disp.setTotalVagas(disp.getTotalVagas() - 1);
        disponibilidadeRepository.save(disp);
    }

    @Transactional
    public void cancelarPorDisponibilidade(Integer idDisponibilidade, Usuario usuarioLogado) {
        if (usuarioLogado == null) throw new RuntimeException("Usuário não autenticado.");

        Agendamento ag = agendamentoRepository
                .findFirstByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
                        usuarioLogado.getId(), idDisponibilidade, SituacaoAgendamento.CANCELADO)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        ag.setSituacao(SituacaoAgendamento.CANCELADO);
        agendamentoRepository.save(ag);

        Disponibilidade disp = ag.getDisponibilidade();
        disp.setTotalVagas(disp.getTotalVagas() + 1);
        disponibilidadeRepository.save(disp);
    }

    public List<Agendamento> listarMeusAgendamentos(Usuario usuario) {
        return agendamentoRepository.listarTodosPorUsuarioComJoins(usuario.getId());
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