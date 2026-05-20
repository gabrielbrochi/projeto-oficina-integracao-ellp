package com.ellp.oficinas.dto;

public record AlunoResponse(
    Long id,
    String nome,
    String ra,
    String emailInstitucional,
    String telefone,
    String dataNascimento,
    String curso
) {}
