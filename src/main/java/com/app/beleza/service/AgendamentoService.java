package com.app.beleza.service;

import com.app.beleza.model.dto.AgendamentoDTO;
import com.app.beleza.respository.AgendamentoRepository;
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
import com.app.beleza.respository.DisponibilidadeRepository;
import com.app.beleza.respository.InstrutorRepository;
import com.app.beleza.respository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
public class AgendamentoService {

    @Autowired private AgendamentoRepository agendamentoRepository;
    @Autowired private DisponibilidadeRepository disponibilidadeRepository;
    @Autowired private ModeloRepository modeloRepository;
    @Autowired private InstrutorRepository instrutorRepository;
    @Autowired private Clock clock;


    @Transactional
    @CacheEvict(value = "agendamentosUsuario", allEntries = true)
    public void confirmarAgendamentosPendentes(Usuario usuarioLogado) {
        if (usuarioLogado == null) return;

        List<Agendamento> todosAgendamentos = agendamentoRepository.listarTodosPorUsuarioComJoins(usuarioLogado.getId());

        for (Agendamento ag : todosAgendamentos) {
            if (ag.getSituacao() == SituacaoAgendamento.PENDENTE) {
                ag.setSituacao(SituacaoAgendamento.CONFIRMADO);
                agendamentoRepository.save(ag);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SessaoAtendimentoDTO> listarPorDisponibilidade(Integer idProdutoUnidade, Usuario usuarioLogado) {
        List<SessaoAtendimentoDTO> lista = new ArrayList<>();

        LocalDate hoje = LocalDate.now(clock);
        LocalTime agora = LocalTime.now(clock);

        List<Disponibilidade> disponibilidades =
                disponibilidadeRepository.listarPorProdutoUnidadeAPartirDeAgora(
                        idProdutoUnidade, hoje, agora);

        for (Disponibilidade d : disponibilidades) {
            boolean jaAgendou = false;
            if (usuarioLogado != null) {
                jaAgendou = agendamentoRepository.existsByUsuarioIdAndDisponibilidadeIdAndSituacaoNot(
                        usuarioLogado.getId(), d.getId(), SituacaoAgendamento.CANCELADO);
            }

            SessaoAtendimentoDTO dto = new SessaoAtendimentoDTO(
                    d.getId().longValue(),
                    d.getDataDisponibilidade() != null
                            ? d.getDataDisponibilidade().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            : "",
                    d.getHoraInicio() != null
                            ? d.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm"))
                            : "",
                    d.getTotalVagas() != null ? d.getTotalVagas() : 0,
                    jaAgendou
            );
            lista.add(dto);
        }

        return lista;
    }

    @Transactional
    @CacheEvict(value = "agendamentosUsuario", allEntries = true)
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
        boolean conflitoDeHorario = agendamentoRepository.existsConflitoDeHorario(
                usuarioLogado.getId(),
                SituacaoAgendamento.CANCELADO,
                disp.getDataDisponibilidade(),
                disp.getHoraInicio(),
                disp.getHoraFim());
        if (conflitoDeHorario) {
            throw new RuntimeException("Você já possui um agendamento nesse horário. Escolha outro horário para evitar conflitos entre unidades.");
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

    // CANCELAR POR DISPONIBILIDADE (Usado no modal da Home)
    @Transactional
    @CacheEvict(value = "agendamentosUsuario", allEntries = true)
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

    // CANCELAR POR ID DO AGENDAMENTO (Usado na tela de Perfil Paginada)
    @Transactional
    @CacheEvict(value = "agendamentosUsuario", allEntries = true)
    public void cancelar(Integer idAgendamento, Usuario usuarioLogado) {
        if (usuarioLogado == null) throw new RuntimeException("Usuário não autenticado.");

        Agendamento ag = agendamentoRepository.findById(idAgendamento)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado."));

        if (ag.getUsuario() == null || !ag.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Esse agendamento não é seu.");
        }

        if (ag.getSituacao() == SituacaoAgendamento.CANCELADO) {
            throw new RuntimeException("Esse agendamento já está cancelado.");
        }

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
    public Page<AgendamentoDTO> listarAgendamentosPorUsuarioPaginado(Integer idModelo, Integer idUsuario, int pagina, int tamanho, String filtroStr) {

        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by("id").descending());

        // Converte o texto da URL para o Enum. Se vier vazio, passa null (traz todos)
        SituacaoAgendamento filtroEnum = null;
        if (filtroStr != null && !filtroStr.trim().isEmpty() && !filtroStr.equals("TODOS")) {
            try {
                filtroEnum = SituacaoAgendamento.valueOf(filtroStr);
            } catch (IllegalArgumentException e) {
                // Se mandarem um filtro inválido na URL, ignora e traz todos
            }
        }

        return agendamentoRepository.buscarPaginadoComFiltro(idModelo, filtroEnum, pageable)
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
