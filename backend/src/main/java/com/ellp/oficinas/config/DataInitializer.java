package com.ellp.oficinas.config;

import com.ellp.oficinas.entity.Usuario;
import com.ellp.oficinas.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setSenha(passwordEncoder.encode("admin"));
            admin.setRole(Usuario.Role.ADMINISTRADOR);
            admin.setPrimeiroAcesso(true);
            usuarioRepository.save(admin);
        }
    }
}
