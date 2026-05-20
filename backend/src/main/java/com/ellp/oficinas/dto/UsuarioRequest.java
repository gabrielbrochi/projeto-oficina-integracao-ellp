package com.ellp.oficinas.dto;

import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
    @NotBlank String username,
    @NotBlank String senha
) {}
