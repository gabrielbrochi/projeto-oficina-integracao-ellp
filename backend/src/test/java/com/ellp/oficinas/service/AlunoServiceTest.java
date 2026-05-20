package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.AlunoRequest;
import com.ellp.oficinas.dto.AlunoResponse;
import com.ellp.oficinas.entity.Aluno;
import com.ellp.oficinas.repository.AlunoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepository alunoRepository;

    @InjectMocks
    private AlunoService alunoService;

    @Test
    void deveCriarAlunoComSucesso() {
        AlunoRequest request = new AlunoRequest(
            "Maria", "2267845", "maria@alunos.utfpr.edu.br",
            "99999-9999", null, "Engenharia de Software"
        );

        Aluno salvo = new Aluno();
        salvo.setId(1L);
        salvo.setNome("Maria");
        salvo.setRa("2267845");
        salvo.setCurso("Engenharia de Software");
        when(alunoRepository.save(any())).thenReturn(salvo);

        AlunoResponse resultado = alunoService.criar(request);

        assertNotNull(resultado);
        assertEquals("Maria", resultado.nome());
        assertEquals(1L, resultado.id());
        assertEquals("Engenharia de Software", resultado.curso());
    }

    @Test
    void deveListarAlunos() {
        Aluno aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Pedro");
        when(alunoRepository.findAll()).thenReturn(List.of(aluno));

        List<AlunoResponse> resultado = alunoService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Pedro", resultado.get(0).nome());
    }

    @Test
    void deveLancarExcecaoAoBuscarAlunoInexistente() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> alunoService.buscarPorId(99L));
    }

    @Test
    void deveDeletarAluno() {
        when(alunoRepository.existsById(1L)).thenReturn(true);

        alunoService.deletar(1L);

        verify(alunoRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarAlunoInexistente() {
        when(alunoRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> alunoService.deletar(99L));
    }
}
