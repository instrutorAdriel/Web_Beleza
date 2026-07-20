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

    public List<SessaoAtendimentoDTO> listarPorServico(Long servicoId, Usuario usuarioLogado) {
        List<SessaoAtendimento> sessoes = repository.findByServicoId(servicoId);

        return sessoes.stream().map(sessao -> {
            // 1. Calcula se o agendamento pertence ao usuário logado
            boolean pertenceAoUsuarioLogado = usuarioLogado != null
                    && sessao.getUsuario() != null
                    && sessao.getUsuario().getId().equals(usuarioLogado.getId());

            // 2. Cria o DTO passando TODOS os 5 argumentos direto no construtor
            return new SessaoAtendimentoDTO(
                    sessao.getId(),
                    sessao.getDataAtendimento().toString(), // Ou sessao.getDataAtendimento() se já for String
                    sessao.getHorarioInicial().toString(),   // Ou sessao.getHorarioInicial() se já for String
                    sessao.getVagasDisponiveis(),
                    pertenceAoUsuarioLogado
            );
        }).collect(Collectors.toList());
    }

    public void agendarSessao(Long id, Usuario usuario) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        if (sessao.getVagasDisponiveis() <= 0) {
            throw new RuntimeException("Não há vagas disponíveis nesse horário");
        }

        // VÍNCULO REAL AQUI:
        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() - 1);
        sessao.setAgendadoPeloUsuario(true);
        sessao.setUsuario(usuario); // Agora o banco sabe EXATAMENTE quem agendou!

        repository.save(sessao);
    }

    // NOVO MÉTODO METICULOSAMENTE CONSTRUÍDO PARA O SEU CÓDIGO:
    public void cancelarSessao(Long id, Usuario usuarioLogado) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        // Validação de Segurança: Se não há usuário vinculado ou se o ID é diferente do logado, trava!
        if (sessao.getUsuario() == null || !sessao.getUsuario().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Você não tem permissão para cancelar o agendamento de outro usuário.");
        }

        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() + 1);
        sessao.setAgendadoPeloUsuario(false);
        sessao.setUsuario(null); // Desvincula o usuário da sessão

        repository.save(sessao);
    }
}