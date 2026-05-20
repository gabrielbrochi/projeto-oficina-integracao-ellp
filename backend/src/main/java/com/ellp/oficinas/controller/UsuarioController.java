package com.ellp.oficinas.controller;

import com.ellp.oficinas.dto.TrocarSenhaRequest;
import com.ellp.oficinas.dto.UsuarioRequest;
import com.ellp.oficinas.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Void> criar(@Valid @RequestBody UsuarioRequest request) {
        usuarioService.criar(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/senha")
    public ResponseEntity<Void> trocarSenha(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TrocarSenhaRequest request) {
        usuarioService.trocarSenha(userDetails.getUsername(), request);
        return ResponseEntity.ok().build();
    }
}
