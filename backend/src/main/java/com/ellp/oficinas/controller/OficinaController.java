package com.ellp.oficinas.controller;

import com.ellp.oficinas.dto.OficinaRequest;
import com.ellp.oficinas.dto.OficinaResponse;
import com.ellp.oficinas.service.OficinaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/oficinas")
public class OficinaController {

    private final OficinaService oficinaService;

    public OficinaController(OficinaService oficinaService) {
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public List<OficinaResponse> listar() {
        return oficinaService.listar();
    }

    @GetMapping("/{id}")
    public OficinaResponse buscar(@PathVariable Long id) {
        return oficinaService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<OficinaResponse> criar(@Valid @RequestBody OficinaRequest request) {
        return ResponseEntity.status(201).body(oficinaService.criar(request));
    }

    @PutMapping("/{id}")
    public OficinaResponse atualizar(@PathVariable Long id, @Valid @RequestBody OficinaRequest request) {
        return oficinaService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        oficinaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
