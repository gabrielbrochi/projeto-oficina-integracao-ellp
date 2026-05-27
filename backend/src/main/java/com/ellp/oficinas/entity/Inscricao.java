package com.ellp.oficinas.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inscricoes")
public class Inscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "oficina_id", nullable = false)
    private Oficina oficina;

    @Column(nullable = false)
    private boolean presente = false;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public Oficina getOficina() { return oficina; }
    public void setOficina(Oficina oficina) { this.oficina = oficina; }
    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }
}
