package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Estudiantes")
public class EstudianteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Estudiante")
    private Long id;

    @Column(name = "Nombre_Estudiante", nullable = false)
    private String nombre;

    @Column(name = "Teléfono", nullable = false)
    private String telefono;

    @Column(name = "Correo", nullable = false)
    private String correo;

    @ManyToMany
    @JoinTable(
            name = "Encargado_Estudiante",
            joinColumns = @JoinColumn(name = "ID_Estudiante"),
            inverseJoinColumns = @JoinColumn(name = "ID_Encargado")
    )
    private Set<EncargadoEntity> encargados = new HashSet<>();

    public EstudianteEntity() {
        this.encargados = new HashSet<>();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreEstudiante() {
        return nombre;
    }

    public void setNombreEstudiante(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefonoEstudiante() {
        return telefono;
    }

    public void setTelefonoEstudiante(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoEstudiante() {
        return correo;
    }

    public void setCorreoEstudiante(String correo) {
        this.correo = correo;
    }

    public Set<EncargadoEntity> getEncargados() {
        return encargados;
    }

    public void setEncargados(Set<EncargadoEntity> encargados) {
        this.encargados = encargados;
    }

    @Transient
    private List<Map<String, String>> historialAcademico = new ArrayList<>();

    public List<Map<String, String>> getHistorialAcademico() {
        return historialAcademico;
    }

    public void setHistorialAcademico(List<Map<String, String>> historialAcademico) {
        this.historialAcademico = historialAcademico;
    }

    // equals y hashCode por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EstudianteEntity)) {
            return false;
        }
        EstudianteEntity that = (EstudianteEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
