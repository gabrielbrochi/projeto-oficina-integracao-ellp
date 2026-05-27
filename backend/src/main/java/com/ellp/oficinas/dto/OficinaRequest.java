package com.ellp.oficinas.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OficinaRequest(
    @NotBlank String nome,
    String descricao,
    @NotBlank String data,
    @NotNull @Min(value = 1, message = "Carga horária deve ser maior que zero") Integer cargaHoraria
) {}
