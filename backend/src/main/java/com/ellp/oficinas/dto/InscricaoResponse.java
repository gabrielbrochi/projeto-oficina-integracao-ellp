package com.ellp.oficinas.dto;

public record InscricaoResponse(
    Long id,
    Long alunoId,
    String alunoNome,
    Long oficinaId,
    String oficinaNome,
    boolean presente
) {}
