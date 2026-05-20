package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.AlunoRequest;
import com.ellp.oficinas.dto.AlunoResponse;
import com.ellp.oficinas.entity.Aluno;
import com.ellp.oficinas.repository.AlunoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    public List<AlunoResponse> listar() {
        return alunoRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AlunoResponse buscarPorId(Long id) {
        return toResponse(alunoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado")));
    }

    public AlunoResponse criar(AlunoRequest request) {
        Aluno aluno = new Aluno();
        preencherAluno(aluno, request);
        return toResponse(alunoRepository.save(aluno));
    }

    public AlunoResponse atualizar(Long id, AlunoRequest request) {
        Aluno aluno = alunoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Aluno não encontrado"));
        preencherAluno(aluno, request);
        return toResponse(alunoRepository.save(aluno));
    }

    public void deletar(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new IllegalArgumentException("Aluno não encontrado");
        }
        alunoRepository.deleteById(id);
    }

    private void preencherAluno(Aluno aluno, AlunoRequest request) {
        aluno.setNome(request.nome());
        aluno.setRa(request.ra());
        aluno.setEmailInstitucional(request.emailInstitucional());
        aluno.setTelefone(request.telefone());
        aluno.setCurso(request.curso());
        if (request.dataNascimento() != null && !request.dataNascimento().isBlank()) {
            LocalDate data = LocalDate.parse(request.dataNascimento());
            if (!data.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Data de nascimento deve ser no passado");
            }
            aluno.setDataNascimento(data);
        }
    }

    private AlunoResponse toResponse(Aluno aluno) {
        return new AlunoResponse(
            aluno.getId(),
            aluno.getNome(),
            aluno.getRa(),
            aluno.getEmailInstitucional(),
            aluno.getTelefone(),
            aluno.getDataNascimento() != null ? aluno.getDataNascimento().toString() : null,
            aluno.getCurso()
        );
    }
}
