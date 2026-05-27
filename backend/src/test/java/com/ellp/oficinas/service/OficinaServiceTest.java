package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.OficinaRequest;
import com.ellp.oficinas.dto.OficinaResponse;
import com.ellp.oficinas.entity.Oficina;
import com.ellp.oficinas.repository.OficinaRepository;
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
class OficinaServiceTest {

    @Mock
    private OficinaRepository oficinaRepository;

    @InjectMocks
    private OficinaService oficinaService;

    private final String dataFutura = LocalDate.now().plusDays(10).toString();
    private final String dataPassada = LocalDate.now().minusDays(1).toString();

    @Test
    void deveCriarOficinaComSucesso() {
        OficinaRequest request = new OficinaRequest("Java Básico", "Introdução ao Java", dataFutura, 8);

        Oficina salva = new Oficina();
        salva.setId(1L);
        salva.setNome("Java Básico");
        salva.setDescricao("Introdução ao Java");
        salva.setData(LocalDate.parse(dataFutura));
        salva.setCargaHoraria(8);

        when(oficinaRepository.existsByNome("Java Básico")).thenReturn(false);
        when(oficinaRepository.save(any())).thenReturn(salva);

        OficinaResponse resultado = oficinaService.criar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("Java Básico", resultado.nome());
        assertEquals("Introdução ao Java", resultado.descricao());
        assertEquals(8, resultado.cargaHoraria());
        verify(oficinaRepository).save(any());
    }

    @Test
    void deveRejeitarNomeDuplicadoAoCriar() {
        OficinaRequest request = new OficinaRequest("Java Básico", "Descrição", dataFutura, 8);
        when(oficinaRepository.existsByNome("Java Básico")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> oficinaService.criar(request));
        verify(oficinaRepository, never()).save(any());
    }

    @Test
    void deveRejeitarDataNoPassadoAoCriar() {
        OficinaRequest request = new OficinaRequest("Java Básico", "Descrição", dataPassada, 8);
        when(oficinaRepository.existsByNome("Java Básico")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> oficinaService.criar(request));
        verify(oficinaRepository, never()).save(any());
    }

    @Test
    void deveListarOficinas() {
        Oficina oficina = new Oficina();
        oficina.setId(1L);
        oficina.setNome("Python Avançado");
        oficina.setData(LocalDate.parse(dataFutura));
        oficina.setCargaHoraria(16);

        when(oficinaRepository.findAll()).thenReturn(List.of(oficina));

        List<OficinaResponse> resultado = oficinaService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Python Avançado", resultado.get(0).nome());
        assertEquals(16, resultado.get(0).cargaHoraria());
    }

    @Test
    void deveBuscarOficinaPorId() {
        Oficina oficina = new Oficina();
        oficina.setId(1L);
        oficina.setNome("Spring Boot");
        oficina.setData(LocalDate.parse(dataFutura));
        oficina.setCargaHoraria(12);

        when(oficinaRepository.findById(1L)).thenReturn(Optional.of(oficina));

        OficinaResponse resultado = oficinaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("Spring Boot", resultado.nome());
        assertEquals(12, resultado.cargaHoraria());
    }

    @Test
    void deveLancarExcecaoAoBuscarOficinaInexistente() {
        when(oficinaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> oficinaService.buscarPorId(99L));
    }

    @Test
    void deveAtualizarOficinaComSucesso() {
        Oficina oficina = new Oficina();
        oficina.setId(1L);
        oficina.setNome("Java Básico");
        oficina.setData(LocalDate.parse(dataFutura));
        oficina.setCargaHoraria(8);

        OficinaRequest request = new OficinaRequest("Java Avançado", "Curso avançado", dataFutura, 16);

        when(oficinaRepository.findById(1L)).thenReturn(Optional.of(oficina));
        when(oficinaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        OficinaResponse resultado = oficinaService.atualizar(1L, request);

        assertEquals("Java Avançado", resultado.nome());
        assertEquals(16, resultado.cargaHoraria());
        verify(oficinaRepository).save(oficina);
    }

    @Test
    void deveLancarExcecaoAoAtualizarOficinaInexistente() {
        OficinaRequest request = new OficinaRequest("Nome", "Desc", dataFutura, 4);
        when(oficinaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> oficinaService.atualizar(99L, request));
        verify(oficinaRepository, never()).save(any());
    }

    @Test
    void deveDeletarOficina() {
        when(oficinaRepository.existsById(1L)).thenReturn(true);

        oficinaService.deletar(1L);

        verify(oficinaRepository).deleteById(1L);
    }

    @Test
    void deveLancarExcecaoAoDeletarOficinaInexistente() {
        when(oficinaRepository.existsById(99L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> oficinaService.deletar(99L));
        verify(oficinaRepository, never()).deleteById(any());
    }
}
