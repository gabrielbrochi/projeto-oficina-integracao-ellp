package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.LoginRequest;
import com.ellp.oficinas.dto.LoginResponse;
import com.ellp.oficinas.entity.Usuario;
import com.ellp.oficinas.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setUsername("administrador");
        usuario.setSenha("hashed");
        usuario.setRole(Usuario.Role.ADMINISTRADOR);
        usuario.setPrimeiroAcesso(false);

        when(usuarioRepository.findByUsername("administrador")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha123", "hashed")).thenReturn(true);
        when(jwtService.gerarToken("administrador")).thenReturn("jwt-token");

        LoginResponse response = authService.login(new LoginRequest("administrador", "senha123"));

        assertEquals("jwt-token", response.token());
        assertEquals("administrador", response.username());
        assertFalse(response.primeiroAcesso());
    }

    @Test
    void deveRejeitarSenhaInvalida() {
        Usuario usuario = new Usuario();
        usuario.setUsername("administrador");
        usuario.setSenha("hashed");
        usuario.setRole(Usuario.Role.ADMINISTRADOR);

        when(usuarioRepository.findByUsername("administrador")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("errada", "hashed")).thenReturn(false);

        assertThrows(IllegalArgumentException.class,
            () -> authService.login(new LoginRequest("administrador", "errada")));
    }

    @Test
    void deveRejeitarUsernameInexistente() {
        when(usuarioRepository.findByUsername("naoexiste")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
            () -> authService.login(new LoginRequest("naoexiste", "senha")));
    }
}
