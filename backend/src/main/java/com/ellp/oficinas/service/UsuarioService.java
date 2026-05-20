package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.TrocarSenhaRequest;
import com.ellp.oficinas.dto.UsuarioRequest;
import com.ellp.oficinas.entity.Usuario;
import com.ellp.oficinas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void criar(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setUsername(request.username());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setRole(Usuario.Role.GERENTE_OFICINA);
        usuario.setPrimeiroAcesso(true);
        usuarioRepository.save(usuario);
    }

    public void trocarSenha(String username, TrocarSenhaRequest request) {
        Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
        usuario.setSenha(passwordEncoder.encode(request.novaSenha()));
        usuario.setPrimeiroAcesso(false);
        usuarioRepository.save(usuario);
    }
}
