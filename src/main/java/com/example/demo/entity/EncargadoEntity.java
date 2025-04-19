package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Encargado")
public class EncargadoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Encargado")
    private Long id;

    @Column(name = "Nombre_Encargado", nullable = false)
    private String nombre;

    @Column(name = "Teléfono", nullable = false)
    private String telefono;

    @Column(name = "Correo", nullable = false)
    private String correo;

    @ManyToMany(mappedBy = "encargados")
    private Set<EstudianteEntity> estudiantes = new HashSet<>();

    public EncargadoEntity() {
        this.estudiantes = new HashSet<>();
    }

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombreEncargado() { return nombre; }

    public void setNombreEncargado(String nombre) { this.nombre = nombre; }

    public String getTelefonoEncargado() { return telefono; }

    public void setTelefonoEncargado(String telefono) { this.telefono = telefono; }

    public String getCorreoEncargado() { return correo; }

    public void setCorreoEncargado(String correo) { this.correo = correo; }

    public Set<EstudianteEntity> getEstudiantes() { return estudiantes; }

    public void setEstudiantes(Set<EstudianteEntity> estudiantes) { this.estudiantes = estudiantes; }

    // equals y hashCode por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EncargadoEntity)) return false;
        EncargadoEntity that = (EncargadoEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
