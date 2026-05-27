package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.InscricaoRequest;
import com.ellp.oficinas.dto.InscricaoResponse;
import com.ellp.oficinas.entity.Aluno;
import com.ellp.oficinas.entity.Inscricao;
import com.ellp.oficinas.entity.Oficina;
import com.ellp.oficinas.repository.AlunoRepository;
import com.ellp.oficinas.repository.InscricaoRepository;
import com.ellp.oficinas.repository.OficinaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscricaoServiceTest {

    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    @Mock
    private OficinaRepository oficinaRepository;

    @InjectMocks
    private InscricaoService inscricaoService;

    private Aluno aluno;
    private Oficina oficina;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Maria");
        aluno.setRa("2267845");

        oficina = new Oficina();
        oficina.setId(10L);
        oficina.setNome("Java Básico");
        oficina.setData(LocalDate.now().plusDays(7));
        oficina.setCargaHoraria(8);
    }

    // ─── listarPorOficina ─────────────────────────────────────────────────────

    @Test
    void deveListarInscricoesPorOficina() {
        Inscricao ins1 = new Inscricao();
        ins1.setId(100L);
        ins1.setAluno(aluno);
        ins1.setOficina(oficina);
        ins1.setPresente(false);

        Inscricao ins2 = new Inscricao();
        ins2.setId(101L);
        ins2.setAluno(aluno);
        ins2.setOficina(oficina);
        ins2.setPresente(true);

        when(inscricaoRepository.findByOficinaId(10L)).thenReturn(List.of(ins1, ins2));

        List<InscricaoResponse> resultado = inscricaoService.listarPorOficina(10L);

        assertEquals(2, resultado.size());
        assertEquals(100L, resultado.get(0).id());
        assertFalse(resultado.get(0).presente());
        assertEquals(101L, resultado.get(1).id());
        assertTrue(resultado.get(1).presente());
        verify(inscricaoRepository).findByOficinaId(10L);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaInscricoesNaOficina() {
        when(inscricaoRepository.findByOficinaId(10L)).thenReturn(List.of());

        List<InscricaoResponse> resultado = inscricaoService.listarPorOficina(10L);

        assertTrue(resultado.isEmpty());
        verify(inscricaoRepository).findByOficinaId(10L);
    }

    // ─── inscrever ────────────────────────────────────────────────────────────

    @Test
    void deveInscreverAlunoComSucesso() {
        InscricaoRequest request = new InscricaoRequest(1L, 10L);

        Inscricao salva = new Inscricao();
        salva.setId(100L);
        salva.setAluno(aluno);
        salva.setOficina(oficina);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(oficinaRepository.findById(10L)).thenReturn(Optional.of(oficina));
        when(inscricaoRepository.existsByAlunoIdAndOficinaId(1L, 10L)).thenReturn(false);
        when(inscricaoRepository.save(any())).thenReturn(salva);

        InscricaoResponse resultado = inscricaoService.inscrever(request);

        assertNotNull(resultado);
        assertEquals(100L, resultado.id());
        assertEquals(1L, resultado.alunoId());
        assertEquals("Maria", resultado.alunoNome());
        assertEquals(10L, resultado.oficinaId());
        assertEquals("Java Básico", resultado.oficinaNome());
        assertFalse(resultado.presente());
        verify(inscricaoRepository).save(any());
    }

    @Test
    void deveLancarExcecaoSeAlunoNaoExistir() {
        InscricaoRequest request = new InscricaoRequest(99L, 10L);
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> inscricaoService.inscrever(request));
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoSeOficinaNaoExistir() {
        InscricaoRequest request = new InscricaoRequest(1L, 99L);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(oficinaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> inscricaoService.inscrever(request));
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoSeAlunoJaEstiverInscrito() {
        InscricaoRequest request = new InscricaoRequest(1L, 10L);
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(oficinaRepository.findById(10L)).thenReturn(Optional.of(oficina));
        when(inscricaoRepository.existsByAlunoIdAndOficinaId(1L, 10L)).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> inscricaoService.inscrever(request));

        assertEquals("Aluno já está inscrito nessa oficina", ex.getMessage());
        verify(inscricaoRepository, never()).save(any());
    }

    // ─── marcarPresenca por ID da inscrição ───────────────────────────────────

    @Test
    void deveMarcarPresencaPorIdComSucesso() {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(100L);
        inscricao.setAluno(aluno);
        inscricao.setOficina(oficina);
        inscricao.setPresente(false);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(inscricaoRepository.save(inscricao)).thenReturn(inscricao);

        InscricaoResponse resultado = inscricaoService.marcarPresenca(100L);

        assertTrue(resultado.presente());
        verify(inscricaoRepository).save(inscricao);
    }

    @Test
    void deveLancarExcecaoAoMarcarPresencaDeInscricaoInexistente() {
        when(inscricaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> inscricaoService.marcarPresenca(99L));
        verify(inscricaoRepository, never()).save(any());
    }

    // ─── marcarPresencaPorAlunoEOficina ───────────────────────────────────────

    @Test
    void deveMarcarPresencaPorAlunoEOficinaComSucesso() {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(100L);
        inscricao.setAluno(aluno);
        inscricao.setOficina(oficina);
        inscricao.setPresente(false);

        when(inscricaoRepository.findByAlunoIdAndOficinaId(1L, 10L)).thenReturn(Optional.of(inscricao));
        when(inscricaoRepository.save(inscricao)).thenReturn(inscricao);

        InscricaoResponse resultado = inscricaoService.marcarPresencaPorAlunoEOficina(1L, 10L);

        assertTrue(resultado.presente());
        assertEquals("Maria", resultado.alunoNome());
        assertEquals("Java Básico", resultado.oficinaNome());
        verify(inscricaoRepository).save(inscricao);
    }

    @Test
    void deveLancarExcecaoAoMarcarPresencaPorAlunoEOficinaInexistente() {
        when(inscricaoRepository.findByAlunoIdAndOficinaId(99L, 10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> inscricaoService.marcarPresencaPorAlunoEOficina(99L, 10L));
        verify(inscricaoRepository, never()).save(any());
    }

    @Test
    void deveMarcarPresencaAlterandoApenasOCampoPresente() {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(100L);
        inscricao.setAluno(aluno);
        inscricao.setOficina(oficina);
        inscricao.setPresente(false);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(inscricaoRepository.save(inscricao)).thenReturn(inscricao);

        inscricaoService.marcarPresenca(100L);

        assertTrue(inscricao.isPresente());
        assertEquals(aluno, inscricao.getAluno());
        assertEquals(oficina, inscricao.getOficina());
    }
}
