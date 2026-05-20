package com.ellp.oficinas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AlunoRequest(
    @NotBlank String nome,
    String ra,
    String emailInstitucional,
    @Size(min = 10, message = "Telefone deve ter no mínimo 10 dígitos") String telefone,
    String dataNascimento,
    String curso
) {}
