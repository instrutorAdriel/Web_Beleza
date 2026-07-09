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

    public List<SessaoAtendimentoDTO> listarPorServico(Long servicoId) {
        List<SessaoAtendimento> sessoes = repository.findByServicoId(servicoId);

        return sessoes.stream().map(s -> new SessaoAtendimentoDTO(
                s.getId(),
                s.getDataAtendimento(),
                s.getHorarioInicial(),
                s.getVagasDisponiveis(),
                s.isAgendadoPeloUsuario()
        )).collect(Collectors.toList());
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
    public void cancelarSessao(Long id, Usuario usuario) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        // Segurança extra: impede que o usuário A cancele a sessão do usuário B
        if (sessao.getUsuario() == null || !sessao.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Você não tem permissão para cancelar este agendamento.");
        }

        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() + 1);
        sessao.setAgendadoPeloUsuario(false);
        sessao.setUsuario(null); // Remove o vínculo do usuário daquela vaga

        repository.save(sessao);
    }
}