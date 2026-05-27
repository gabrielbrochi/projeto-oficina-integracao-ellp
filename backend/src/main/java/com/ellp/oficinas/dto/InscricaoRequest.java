package com.ellp.oficinas.dto;

import jakarta.validation.constraints.NotNull;

public record InscricaoRequest(
    @NotNull Long alunoId,
    @NotNull Long oficinaId
) {}
