package com.ellp.oficinas.service;

import com.ellp.oficinas.entity.Inscricao;
import com.ellp.oficinas.repository.InscricaoRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CertificadoService {

    private final InscricaoRepository inscricaoRepository;
    private final JasperReportPort jasperReportPort;

    public CertificadoService(InscricaoRepository inscricaoRepository,
                               JasperReportPort jasperReportPort) {
        this.inscricaoRepository = inscricaoRepository;
        this.jasperReportPort = jasperReportPort;
    }

    public byte[] gerarCertificado(Long inscricaoId) {
        Inscricao inscricao = inscricaoRepository.findById(inscricaoId)
            .orElseThrow(() -> new IllegalArgumentException("Inscrição não encontrada"));

        if (!inscricao.isPresente()) {
            throw new IllegalArgumentException("Aluno não tem presença confirmada nessa oficina");
        }

        Map<String, Object> parametros = new HashMap<>();
        parametros.put("nomeAluno", inscricao.getAluno().getNome());
        parametros.put("nomeOficina", inscricao.getOficina().getNome());
        parametros.put("cargaHoraria", inscricao.getOficina().getCargaHoraria());

        try {
            return jasperReportPort.gerar(parametros);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar certificado em PDF", e);
        }
    }
}
