/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.GeneracionEntity;
import com.example.demo.entity.GrupoEntity;
import com.example.demo.entity.SeccionEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.dialect.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 *
 * @author guerr
 */
@Service
public class MatriculaService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SeccionEntity> obtenerSeccionesPorPeriodo(String semestre, int anio) {
        return jdbcTemplate.execute((Connection con) -> {
            List<SeccionEntity> secciones = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call secciones_por_periodo(?, ?, ?) }")) {
                cs.setString(1, semestre);
                cs.setInt(2, anio);
                cs.registerOutParameter(3, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        SeccionEntity seccion = new SeccionEntity();
                        seccion.setId(rs.getLong("ID_Seccion"));
                        seccion.setNombreSeccion(rs.getString("Nombre_Seccion"));
                        secciones.add(seccion);
                    }
                }
            }
            return secciones;
        });
    }

    public List<Map<String, Object>> obtenerHorarioPorSeccion(Long idSeccion) {
        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> horario = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call mostrar_horario_seccion(?, ?) }")) {
                cs.setLong(1, idSeccion);
                cs.registerOutParameter(2, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        Map<String, Object> fila = new HashMap<>();
                        fila.put("dia", rs.getString("Dia"));
                        fila.put("hora", rs.getString("Hora"));
                        fila.put("curso", rs.getString("Curso"));
                        fila.put("seccion", rs.getString("Seccion"));
                        horario.add(fila);
                    }
                }
            }
            return horario;
        });
    }

    public List<Map<String, Object>> obtenerEstudiantesPorSeccionPeriodo(Long idSeccion, String semestre, int anio) {

        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> estudiantes = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call obtener_estudiantes_por_seccion_periodo(?, ?, ?, ?) }")) {
                cs.setLong(1, idSeccion);
                cs.setString(2, semestre);
                cs.setInt(3, anio);
                cs.registerOutParameter(4, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    while (rs.next()) {
                        Map<String, Object> estudiante = new HashMap<>();
                        estudiante.put("nombre", rs.getString("nombre"));  // Cambiado a "nombre"
                        estudiantes.add(estudiante);
                    }
                }
            }
            return estudiantes;
        });
    }

    public List<Map<String, Object>> obtenerProfesoresPorSeccionPeriodo(Long idSeccion, String semestre, int anio) {
        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> profesores = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call obtener_profesores_por_seccion_periodo(?, ?, ?, ?) }")) {
                cs.setLong(1, idSeccion);
                cs.setString(2, semestre);
                cs.setInt(3, anio);
                cs.registerOutParameter(4, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    while (rs.next()) {
                        Map<String, Object> profesor = new HashMap<>();
                        profesor.put("nombre", rs.getString("nombre"));  // Usando "nombre"
                        profesores.add(profesor);
                    }
                }
            }
            return profesores;
        });
    }

}
