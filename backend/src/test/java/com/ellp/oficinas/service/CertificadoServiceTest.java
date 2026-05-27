package com.ellp.oficinas.service;

import com.ellp.oficinas.entity.Aluno;
import com.ellp.oficinas.entity.Inscricao;
import com.ellp.oficinas.entity.Oficina;
import com.ellp.oficinas.repository.InscricaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CertificadoServiceTest {

    // JasperReportPort é mockado — nenhum arquivo .jrxml é lido nos testes,
    // nenhuma compilação Jasper ocorre. A pipeline de build não depende de PDF real.
    @Mock
    private InscricaoRepository inscricaoRepository;

    @Mock
    private JasperReportPort jasperReportPort;

    @InjectMocks
    private CertificadoService certificadoService;

    private Aluno aluno;
    private Oficina oficina;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Maria Silva");

        oficina = new Oficina();
        oficina.setId(10L);
        oficina.setNome("Java Básico");
        oficina.setData(LocalDate.now().plusDays(7));
        oficina.setCargaHoraria(8);
    }

    // ─── Regra: inscrição deve existir ────────────────────────────────────────

    @Test
    void deveLancarExcecaoSeInscricaoNaoExistir() {
        when(inscricaoRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> certificadoService.gerarCertificado(99L));

        assertEquals("Inscrição não encontrada", ex.getMessage());
        verifyNoInteractions(jasperReportPort);
    }

    // ─── Regra: aluno deve ter presença confirmada ────────────────────────────

    @Test
    void deveLancarExcecaoSeAlunoNaoTemPresenca() {
        Inscricao inscricao = inscricaoComPresenca(false);
        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> certificadoService.gerarCertificado(100L));

        assertEquals("Aluno não tem presença confirmada nessa oficina", ex.getMessage());
        verifyNoInteractions(jasperReportPort);
    }

    @Test
    void naoDeveGerarPdfSeAlunoAusenteIndependenteDaOficina() {
        Inscricao inscricao = inscricaoComPresenca(false);
        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));

        assertThrows(IllegalArgumentException.class,
            () -> certificadoService.gerarCertificado(100L));

        // Garante que o Jasper nunca foi acionado
        verify(jasperReportPort, never()).gerar(any());
    }

    // ─── Fluxo feliz ──────────────────────────────────────────────────────────

    @Test
    void deveRetornarPdfQuandoPresencaConfirmada() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);
        byte[] pdfEsperado = new byte[]{37, 80, 68, 70}; // "%PDF" em bytes

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenReturn(pdfEsperado);

        byte[] resultado = certificadoService.gerarCertificado(100L);

        assertArrayEquals(pdfEsperado, resultado);
        verify(jasperReportPort).gerar(any());
    }

    // ─── Parâmetros passados ao Jasper ────────────────────────────────────────

    @Test
    void devePassarNomeAlunoCorretamenteParaOJasper() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenReturn(new byte[]{1});

        certificadoService.gerarCertificado(100L);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jasperReportPort).gerar(captor.capture());
        assertEquals("Maria Silva", captor.getValue().get("nomeAluno"));
    }

    @Test
    void devePassarNomeOficinaCorretamenteParaOJasper() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenReturn(new byte[]{1});

        certificadoService.gerarCertificado(100L);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jasperReportPort).gerar(captor.capture());
        assertEquals("Java Básico", captor.getValue().get("nomeOficina"));
    }

    @Test
    void devePassarCargaHorariaCorretamenteParaOJasper() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenReturn(new byte[]{1});

        certificadoService.gerarCertificado(100L);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jasperReportPort).gerar(captor.capture());
        assertEquals(8, captor.getValue().get("cargaHoraria"));
    }

    @Test
    void devePassarTodosOsTresParametrosJuntos() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenReturn(new byte[]{1});

        certificadoService.gerarCertificado(100L);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(jasperReportPort).gerar(captor.capture());

        Map<String, Object> params = captor.getValue();
        assertAll(
            () -> assertEquals("Maria Silva", params.get("nomeAluno")),
            () -> assertEquals("Java Básico", params.get("nomeOficina")),
            () -> assertEquals(8, params.get("cargaHoraria"))
        );
    }

    // ─── Tratamento de falha no Jasper ────────────────────────────────────────

    @Test
    void deveEncapsularExcecaoDoJasperEmRuntimeException() throws Exception {
        Inscricao inscricao = inscricaoComPresenca(true);

        when(inscricaoRepository.findById(100L)).thenReturn(Optional.of(inscricao));
        when(jasperReportPort.gerar(any())).thenThrow(new Exception("Falha interna no Jasper"));

        RuntimeException ex = assertThrows(RuntimeException.class,
            () -> certificadoService.gerarCertificado(100L));

        assertEquals("Erro ao gerar certificado em PDF", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Inscricao inscricaoComPresenca(boolean presente) {
        Inscricao inscricao = new Inscricao();
        inscricao.setId(100L);
        inscricao.setAluno(aluno);
        inscricao.setOficina(oficina);
        inscricao.setPresente(presente);
        return inscricao;
    }
}
