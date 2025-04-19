package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Periodo")
public class PeriodoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Periodo")
    private Long id;

    @Column(name = "ID_Matricula", nullable = false)
    private Long idMatricula;

    @Column(name = "Semestre", nullable = false)
    private String semestre;

    @Column(name = "Año", nullable = false)
    private int anio;

    public PeriodoEntity() {}

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getIdMatricula() { return idMatricula; }

    public void setIdMatricula(Long idMatricula) { this.idMatricula = idMatricula; }

    public String getSemestre() { return semestre; }

    public void setSemestre(String semestre) { this.semestre = semestre; }

    public int getAnio() { return anio; }

    public void setAnio(int anio) { this.anio = anio; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodoEntity)) return false;
        PeriodoEntity that = (PeriodoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
