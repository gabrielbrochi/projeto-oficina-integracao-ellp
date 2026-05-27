package com.ellp.oficinas.controller;

import com.ellp.oficinas.dto.InscricaoRequest;
import com.ellp.oficinas.dto.InscricaoResponse;
import com.ellp.oficinas.service.InscricaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {

    private final InscricaoService inscricaoService;

    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }

    @GetMapping("/oficina/{oficinaId}")
    public List<InscricaoResponse> listarPorOficina(@PathVariable Long oficinaId) {
        return inscricaoService.listarPorOficina(oficinaId);
    }

    @PostMapping
    public ResponseEntity<InscricaoResponse> inscrever(@Valid @RequestBody InscricaoRequest request) {
        return ResponseEntity.status(201).body(inscricaoService.inscrever(request));
    }

    @PatchMapping("/{id}/presenca")
    public InscricaoResponse marcarPresenca(@PathVariable Long id) {
        return inscricaoService.marcarPresenca(id);
    }

    @PatchMapping("/aluno/{alunoId}/oficina/{oficinaId}/presenca")
    public InscricaoResponse marcarPresencaPorAlunoEOficina(
            @PathVariable Long alunoId,
            @PathVariable Long oficinaId) {
        return inscricaoService.marcarPresencaPorAlunoEOficina(alunoId, oficinaId);
    }
}
