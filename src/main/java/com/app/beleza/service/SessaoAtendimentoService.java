package com.app.beleza.service;

import com.app.beleza.model.Agendamento;
import com.app.beleza.model.Disponibilidade;
import com.app.beleza.model.Modelo;
import com.app.beleza.model.SessaoAtendimentoDTO;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.AgendamentoRepository;
import com.app.beleza.respository.DisponibilidadeRepository;
import com.app.beleza.respository.ModeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SessaoAtendimentoService {

    private static final String CANCELADO = "Cancelado";
    private static final String CONFIRMADO = "Confirmado";

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private ModeloRepository modeloRepository;

    @Transactional(readOnly = true)
    public List<SessaoAtendimentoDTO> listarPorServico(Integer produtoUnidadeId, Usuario usuarioLogado) {
        List<Disponibilidade> sessoes = disponibilidadeRepository.findByProdutoUnidadeId(produtoUnidadeId);

        return sessoes.stream().map(sessao -> {
            boolean pertenceAoUsuarioLogado = usuarioLogado != null
                    && agendamentoRepository
                    .findByDisponibilidadeIdAndUsuarioIdAndSituacaoNot(
                            sessao.getId(), usuarioLogado.getId(), CANCELADO)
                    .isPresent();

            return new SessaoAtendimentoDTO(
                    sessao.getId(),
                    sessao.getDataAtendimento().toString(),
                    sessao.getHorarioInicial().toString(),
                    vagasLivres(sessao),
                    pertenceAoUsuarioLogado
            );
        }).collect(Collectors.toList());
    }

    public void agendarSessao(Integer id, Usuario usuario) {
        Disponibilidade sessao = disponibilidadeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        if (vagasLivres(sessao) <= 0) {
            throw new RuntimeException("Não há vagas disponíveis nesse horário");
        }

        boolean jaAgendou = agendamentoRepository
                .findByDisponibilidadeIdAndUsuarioIdAndSituacaoNot(id, usuario.getId(), CANCELADO)
                .isPresent();

        if (jaAgendou) {
            throw new RuntimeException("Você já agendou este horário!");
        }

        Modelo modelo = modeloRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RuntimeException(
                        "Complete seu cadastro (telefone e data de nascimento) para agendar."));

        Agendamento agendamento = new Agendamento();
        agendamento.setDisponibilidade(sessao);
        agendamento.setUsuario(usuario);
        agendamento.setModelo(modelo);
        agendamento.setSituacaoAgendamento(CONFIRMADO);
        agendamento.setDataHora(LocalDateTime.now());

        agendamentoRepository.save(agendamento);
    }

    public void cancelarSessao(Integer id, Usuario usuarioLogado) {
        Agendamento agendamento = agendamentoRepository
                .findByDisponibilidadeIdAndUsuarioIdAndSituacaoNot(id, usuarioLogado.getId(), CANCELADO)
                .orElseThrow(() -> new RuntimeException(
                        "Você não tem permissão para cancelar este agendamento."));

        // A vaga é devolvida automaticamente: vagasLivres() só conta agendamentos não cancelados
        agendamento.setSituacaoAgendamento(CANCELADO);
        agendamentoRepository.save(agendamento);
    }

    private int vagasLivres(Disponibilidade sessao) {
        long ocupadas = agendamentoRepository
                .countByDisponibilidadeIdAndSituacaoNot(sessao.getId(), CANCELADO);
        return (int) (sessao.getVagasDisponiveis() - ocupadas);
    }
}