package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Generacion")
public class GeneracionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Generacion")
    private Integer id;

    @Column(name = "Generacion", nullable = false, length = 100)
    private String generacion;

    public GeneracionEntity() {}

    // Getters y Setters
    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getGeneracion() { return generacion; }

    public void setGeneracion(String generacion) { this.generacion = generacion; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeneracionEntity)) return false;
        GeneracionEntity that = (GeneracionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
