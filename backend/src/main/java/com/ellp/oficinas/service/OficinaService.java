package com.ellp.oficinas.service;

import com.ellp.oficinas.dto.OficinaRequest;
import com.ellp.oficinas.dto.OficinaResponse;
import com.ellp.oficinas.entity.Oficina;
import com.ellp.oficinas.repository.OficinaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OficinaService {

    private final OficinaRepository oficinaRepository;

    public OficinaService(OficinaRepository oficinaRepository) {
        this.oficinaRepository = oficinaRepository;
    }

    public List<OficinaResponse> listar() {
        return oficinaRepository.findAll().stream().map(this::toResponse).toList();
    }

    public OficinaResponse buscarPorId(Long id) {
        return toResponse(oficinaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Oficina não encontrada")));
    }

    public OficinaResponse criar(OficinaRequest request) {
        if (oficinaRepository.existsByNome(request.nome())) {
            throw new IllegalArgumentException("Já existe uma oficina com esse nome");
        }
        Oficina oficina = new Oficina();
        preencherOficina(oficina, request);
        return toResponse(oficinaRepository.save(oficina));
    }

    public OficinaResponse atualizar(Long id, OficinaRequest request) {
        Oficina oficina = oficinaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Oficina não encontrada"));
        preencherOficina(oficina, request);
        return toResponse(oficinaRepository.save(oficina));
    }

    public void deletar(Long id) {
        if (!oficinaRepository.existsById(id)) {
            throw new IllegalArgumentException("Oficina não encontrada");
        }
        oficinaRepository.deleteById(id);
    }

    private void preencherOficina(Oficina oficina, OficinaRequest request) {
        oficina.setNome(request.nome());
        oficina.setDescricao(request.descricao());
        oficina.setCargaHoraria(request.cargaHoraria());

        LocalDate data = LocalDate.parse(request.data());
        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data da oficina não pode ser no passado");
        }
        oficina.setData(data);
    }

    private OficinaResponse toResponse(Oficina oficina) {
        return new OficinaResponse(
            oficina.getId(),
            oficina.getNome(),
            oficina.getDescricao(),
            oficina.getData() != null ? oficina.getData().toString() : null,
            oficina.getCargaHoraria()
        );
    }
}
