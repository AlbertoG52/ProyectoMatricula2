package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Curso")
public class CursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Curso")
    private Long id;

    @Column(name = "Nombre_Curso", nullable = false)
    private String nombreCurso;

    public CursoEntity() {}

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombreCurso() { return nombreCurso; }

    public void setNombreCurso(String nombreCurso) { this.nombreCurso = nombreCurso; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CursoEntity)) return false;
        CursoEntity that = (CursoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
