package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.TrocarSenhaRequest;
import com.ellp.oficinas.dto.UsuarioRequest;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarGerenteComSucesso() {
        UsuarioRequest request = new UsuarioRequest("gerente1", "senha123");
        when(usuarioRepository.existsByUsername("gerente1")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("hashed");

        usuarioService.criar(request);

        verify(usuarioRepository).save(any());
    }

    @Test
    void deveRejeitarUsernameDuplicado() {
        UsuarioRequest request = new UsuarioRequest("gerente1", "senha123");
        when(usuarioRepository.existsByUsername("gerente1")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.criar(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveTrocarSenhaEMarcarPrimeiroAcessoFalso() {
        Usuario usuario = new Usuario();
        usuario.setUsername("gerente1");
        usuario.setSenha("hashed_antiga");
        usuario.setPrimeiroAcesso(true);

        when(usuarioRepository.findByUsername("gerente1")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("novaSenha")).thenReturn("hashed_nova");

        usuarioService.trocarSenha("gerente1", new TrocarSenhaRequest("novaSenha"));

        assertFalse(usuario.isPrimeiroAcesso());
        verify(usuarioRepository).save(usuario);
    }
}
