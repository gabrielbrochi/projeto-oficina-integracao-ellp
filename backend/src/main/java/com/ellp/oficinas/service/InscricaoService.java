package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.InscricaoRequest;
import com.ellp.oficinas.dto.InscricaoResponse;
import com.ellp.oficinas.entity.Aluno;
import com.ellp.oficinas.entity.Inscricao;
import com.ellp.oficinas.entity.Oficina;
import com.ellp.oficinas.repository.AlunoRepository;
import com.ellp.oficinas.repository.InscricaoRepository;
import com.ellp.oficinas.repository.OficinaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InscricaoService {

    private final InscricaoRepository inscricaoRepository;
    private final AlunoRepository alunoRepository;
    private final OficinaRepository oficinaRepository;

    public InscricaoService(InscricaoRepository inscricaoRepository,
                            AlunoRepository alunoRepository,
                            OficinaRepository oficinaRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.alunoRepository = alunoRepository;
        this.oficinaRepository = oficinaRepository;
    }

    public List<InscricaoResponse> listarPorOficina(Long oficinaId) {
        return inscricaoRepository.findByOficinaId(oficinaId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public InscricaoResponse inscrever(InscricaoRequest request) {
        Aluno aluno = alunoRepository.findById(request.alunoId())
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));

        Oficina oficina = oficinaRepository.findById(request.oficinaId())
            .orElseThrow(() -> new IllegalArgumentException("Oficina não encontrada"));

        if (inscricaoRepository.existsByAlunoIdAndOficinaId(request.alunoId(), request.oficinaId())) {
            throw new IllegalArgumentException("Aluno já está inscrito nessa oficina");
        }

        Inscricao inscricao = new Inscricao();
        inscricao.setAluno(aluno);
        inscricao.setOficina(oficina);
        return toResponse(inscricaoRepository.save(inscricao));
    }

    public InscricaoResponse marcarPresenca(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));
        inscricao.setPresente(true);
        return toResponse(inscricaoRepository.save(inscricao));
    }

    public InscricaoResponse marcarPresencaPorAlunoEOficina(Long alunoId, Long oficinaId) {
        Inscricao inscricao = inscricaoRepository.findByAlunoIdAndOficinaId(alunoId, oficinaId)
            .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada para o aluno e oficina informados"));
        inscricao.setPresente(true);
        return toResponse(inscricaoRepository.save(inscricao));
    }

    private InscricaoResponse toResponse(Inscricao inscricao) {
        return new InscricaoResponse(
            inscricao.getId(),
            inscricao.getAluno().getId(),
            inscricao.getAluno().getNome(),
            inscricao.getOficina().getId(),
            inscricao.getOficina().getNome(),
            inscricao.isPresente()
        );
    }
}
