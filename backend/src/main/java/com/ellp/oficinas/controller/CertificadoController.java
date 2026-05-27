package com.ellp.oficinas.controller;

import com.ellp.oficinas.service.CertificadoService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    private final CertificadoService certificadoService;

    public CertificadoController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping("/inscricao/{inscricaoId}")
    public ResponseEntity<byte[]> gerarCertificado(@PathVariable Long inscricaoId) {
        byte[] pdf = certificadoService.gerarCertificado(inscricaoId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"certificado-inscricao-" + inscricaoId + ".pdf\"")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
