package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "Seccion")
public class SeccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_Seccion")
    private Long id;

    @Column(name = "ID_Generacion", nullable = false)
    private Long idGeneracion;

    @Column(name = "ID_Grupo", nullable = false)
    private Long idGrupo;

    @ManyToOne
    @JoinColumn(name = "ID_Generacion", referencedColumnName = "ID_Generacion", insertable = false, updatable = false)
    private GeneracionEntity generacion;

    @ManyToOne
    @JoinColumn(name = "ID_Grupo", referencedColumnName = "ID_Grupo", insertable = false, updatable = false)
    private GrupoEntity grupo;

    @Transient  // no es un campo de base de datos
    private String nombreSeccion;

    public SeccionEntity() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdGeneracion() {
        return idGeneracion;
    }

    public void setIdGeneracion(Long idGeneracion) {
        this.idGeneracion = idGeneracion;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public GeneracionEntity getGeneracion() {
        return generacion;
    }

    public void setGeneracion(GeneracionEntity generacion) {
        this.generacion = generacion;
    }

    public GrupoEntity getGrupo() {
        return grupo;
    }

    public void setGrupo(GrupoEntity grupo) {
        this.grupo = grupo;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SeccionEntity)) {
            return false;
        }
        SeccionEntity that = (SeccionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
