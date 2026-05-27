package com.ellp.oficinas.dto;

public record OficinaResponse(
    Long id,
    String nome,
    String descricao,
    String data,
    Integer cargaHoraria
) {}
