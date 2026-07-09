package com.app.beleza.service;

import com.app.beleza.model.SessaoAtendimento;
import com.app.beleza.model.SessaoAtendimentoDTO;
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

    public void agendarSessao(Long id){
        SessaoAtendimento sessao = repository.findById(id).orElseThrow(()-> new RuntimeException("Sessão não encontrada!"));

        if(sessao.getVagasDisponiveis() <= 0) {
            throw new RuntimeException("Não há vagas disponíveis nesse horário");
        }

        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() - 1);
        sessao.setAgendadoPeloUsuario(true);

        repository.save(sessao);
    }

    // NOVO MÉTODO METICULOSAMENTE CONSTRUÍDO PARA O SEU CÓDIGO:
    public void cancelarSessao(Long id) {
        SessaoAtendimento sessao = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sessão não encontrada!"));

        // VALIDAÇÃO CRUCIAL: Só cancela se o usuário de fato agendou.
        // Isso impede que vagas subam além do limite original se clicarem várias vezes.
        if (!sessao.isAgendadoPeloUsuario()) {
            throw new RuntimeException("Você não possui um agendamento ativo nesta sessão para cancelar.");
        }

        // Devolve a vaga para o painel
        sessao.setVagasDisponiveis(sessao.getVagasDisponiveis() + 1);

        // MUDANÇA QUE FAZ O BOTÃO VOLTAR A SER LARANJA:
        sessao.setAgendadoPeloUsuario(false);

        repository.save(sessao);
    }
}