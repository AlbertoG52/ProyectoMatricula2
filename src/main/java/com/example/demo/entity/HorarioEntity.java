package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Horario")
public class HorarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Horario")
    private Long id;

    @Column(name = "ID_Dia", nullable = false)
    private Long idDia;

    @Column(name = "ID_Hora", nullable = false)
    private Long idHora;

    public HorarioEntity() {}

    // Getters y Setters
    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Long getIdDia() { return idDia; }

    public void setIdDia(Long idDia) { this.idDia = idDia; }

    public Long getIdHora() { return idHora; }

    public void setIdHora(Long idHora) { this.idHora = idHora; }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorarioEntity)) return false;
        HorarioEntity that = (HorarioEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
