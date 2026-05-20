package com.ellp.oficinas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrocarSenhaRequest(
    @NotBlank @Size(min = 6) String novaSenha
) {}
