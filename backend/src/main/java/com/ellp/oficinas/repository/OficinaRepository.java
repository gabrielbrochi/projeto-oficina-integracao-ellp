package com.ellp.oficinas.repository;

import com.ellp.oficinas.entity.Oficina;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OficinaRepository extends JpaRepository<Oficina, Long> {
    boolean existsByNome(String nome);
}
