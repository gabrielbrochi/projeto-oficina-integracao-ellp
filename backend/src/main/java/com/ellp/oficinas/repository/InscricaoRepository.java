package com.ellp.oficinas.repository;

import com.ellp.oficinas.entity.Inscricao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscricaoRepository extends JpaRepository<Inscricao, Long> {
    boolean existsByAlunoIdAndOficinaId(Long alunoId, Long oficinaId);
    Optional<Inscricao> findByAlunoIdAndOficinaId(Long alunoId, Long oficinaId);
    List<Inscricao> findByOficinaId(Long oficinaId);
}
