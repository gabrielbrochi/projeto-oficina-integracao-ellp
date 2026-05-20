package com.ellp.oficinas.dto;

public record LoginResponse(String token, String username, String role, boolean primeiroAcesso) {}
