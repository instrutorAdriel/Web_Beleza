package com.app.beleza.service;

import org.springframework.stereotype.Service;

@Service
public class AgendamentoService {
    /*
    @Autowired
    private SessaoAtendimentoRepository repository;

    public List<SessaoAtendimentoDTO> listarPorServico(Long servicoId, Usuario usuarioLogado) {
        List<Agendamento> sessoes = repository.findByServicoId(servicoId);

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
        Agendamento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

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
    }

    public void cancelarSessao(Long id, Usuario usuarioLogado) {
        Agendamento sessao = repository.findById(id)
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
    */
}