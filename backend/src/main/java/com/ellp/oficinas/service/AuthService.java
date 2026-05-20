package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.LoginRequest;
import com.ellp.oficinas.dto.LoginResponse;
import com.ellp.oficinas.entity.Usuario;
import com.ellp.oficinas.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));
        return User.withUsername(usuario.getUsername())
            .password(usuario.getSenha())
            .roles(usuario.getRole().name())
            .build();
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(request.username())
            .orElseThrow(() -> new IllegalArgumentException("Usuário ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new IllegalArgumentException("Usuário ou senha inválidos");
        }

        String token = jwtService.gerarToken(usuario.getUsername());
        return new LoginResponse(token, usuario.getUsername(), usuario.getRole().name(), usuario.isPrimeiroAcesso());
    }
}
