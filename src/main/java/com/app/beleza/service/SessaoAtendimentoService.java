package com.app.beleza.service;

import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.SessaoAtendimentoDTO;
import com.app.beleza.model.Usuario;
import com.app.beleza.respository.SessaoAtendimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SessaoAtendimentoService {

    @Autowired
    private SessaoAtendimentoRepository repository;

    @Autowired
    private EmailService emailService;

    public List<SessaoAtendimentoDTO> listarPorServico(Long servicoId, Usuario usuarioLogado) {
        List<SessaoAtendimento> sessoes = repository.findByServicoId(servicoId);

        return sessoes.stream().map(sessao -> {
            // 1. O agendamento pertence ao usuário logado se o ID dele estiver DENTRO da lista de usuários da sessão
            boolean pertenceAoUsuarioLogado = usuarioLogado != null
                    && sessao.getUsuarios() != null
                    && sessao.getUsuarios().stream()
                    .anyMatch(u -> u.getId().equals(usuarioLogado.getId()));

            // 2. Cria o DTO com o boolean correto
            return new SessaoAtendimentoDTO(
                    sessao.getId(),
                    sessao.getDataAtendimento().toString(),
                    sessao.getHorarioInicial().toString(),
                    sessao.getVagasDisponiveis(),
                    pertenceAoUsuarioLogado
            );
        }).collect(Collectors.toList());
    }

    public void agendarSessao(Long id, Usuario usuario) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        IO.println("agendarSessao: " + usuario.getId());

        if (sessao.getVagasDisponiveis() <= 0) {
            throw new RuntimeException("Não há vagas disponíveis nesse horário");
        }

        // Evita que o mesmo usuário agende 2 vezes a mesma sessão
        boolean jaAgendou = sessao.getUsuarios().stream()
                .anyMatch(u -> u.getId().equals(usuario.getId()));

        if (jaAgendou) {
            throw new RuntimeException("Você já agendou este horário!");
        }

        // Adiciona o usuário na lista e reduz a vaga
        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() - 1);
        sessao.getUsuarios().add(usuario);

        repository.save(sessao);

        // NOVO: envia email de confirmação (não trava o agendamento se falhar)
        try {
            emailService.enviarConfirmacao(
                    usuario.getEmail(),
                    usuario.getNomeCompleto(),

                    sessao.getHorarioInicial(),
                    sessao.getDataAtendimento()
            );
        } catch (Exception e) {
            System.out.println("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    }

    public void cancelarSessao(Long id, Usuario usuarioLogado) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        // Verifica se o usuário realmente agendou essa sessão para poder cancelar
        boolean estaNaLista = sessao.getUsuarios().stream()
                .anyMatch(u -> u.getId().equals(usuarioLogado.getId()));

        if (!estaNaLista) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento.");
        }

        // Remove o usuário específico da lista e devolve a vaga
        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() + 1);
        sessao.getUsuarios().removeIf(u -> u.getId().equals(usuarioLogado.getId()));

        repository.save(sessao);
    }
}