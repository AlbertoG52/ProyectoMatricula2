package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Profesor")
public class ProfesorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Profesor")
    private Long id;

    @Column(name = "Nombre_Profesor", nullable = false)
    private String nombre;

    @Column(name = "Correo", nullable = false)
    private String correo;

    @Column(name = "Profesion", nullable = false)
    private String profesion;

    public ProfesorEntity() {
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProfesor() {
        return nombre;
    }

    public void setNombreProfesor(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoProfesor() {
        return correo;
    }

    public void setCorreoProfesor(String correo) {
        this.correo = correo;
    }

    public String getProfesionProfesor() {
        return profesion;
    }

    public void setProfesionProfesor(String profesion) {
        this.profesion = profesion;
    }

    // equals y hashCode por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfesorEntity)) return false;
        ProfesorEntity that = (ProfesorEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
