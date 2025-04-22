/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.GeneracionEntity;
import com.example.demo.entity.GrupoEntity;
import com.example.demo.entity.SeccionEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public List<Map<String, Object>> obtenerEstudiantesNoMatriculados(String semestre, int anio) {
        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> estudiantes = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall(
                    "{ call Obtener_Nombres_Estudiantes_Sin_Matricula(?, ?, ?) }")) {

                cs.setString(1, semestre);
                cs.setInt(2, anio);
                cs.registerOutParameter(3, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        Map<String, Object> estudiante = new HashMap<>();
                        estudiante.put("nombre", rs.getString("Nombre_Estudiante"));
                        estudiantes.add(estudiante);
                    }
                }
            }
            return estudiantes;
        });
    }

    public void matricularEstudiantes(List<String> nombresEstudiantes, String semestre, int anio) {
        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                for (String nombre : nombresEstudiantes) {
                    try (CallableStatement cs = conn.prepareCall(
                            "{call Registrar_Matricula_Completa(?, ?, ?, ?)}")) {

                        cs.setString(1, nombre);
                        cs.setString(2, semestre);
                        cs.setInt(3, anio);
                        cs.registerOutParameter(4, OracleTypes.CURSOR);
                        cs.execute();

                        // Verificar resultado del cursor (1=éxito, 0=error)
                        try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                            if (rs.next() && rs.getInt(1) == 0) {
                                throw new SQLException("Error al matricular a " + nombre);
                            }
                        }
                    }
                }
                conn.commit(); // Confirmar todas las matrículas
            } catch (SQLException e) {
                conn.rollback(); // Revertir en caso de error
                throw e;
            }
            return null;
        });
    }

    public List<Map<String, Object>> obtenerTodasLasSecciones() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> secciones = new ArrayList<>();

            try (CallableStatement cs = conn.prepareCall("{ call PKG_LISTADOS_APP.Mostrar_Todas_Las_Secciones(?) }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Map<String, Object> seccion = new HashMap<>();
                        seccion.put("idSeccion", rs.getLong("ID_Seccion"));
                        seccion.put("seccion", rs.getString("Seccion"));
                        secciones.add(seccion);
                    }
                }
            }
            return secciones;
        });
    }

    public List<Map<String, Object>> obtenerEstudiantesConMatricula(String semestre, int anio) {
        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> estudiantes = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall(
                    "{ call Obtener_Nombres_Estudiantes_Con_Matricula(?, ?, ?) }")) {

                cs.setString(1, semestre);
                cs.setInt(2, anio);
                cs.registerOutParameter(3, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        Map<String, Object> estudiante = new HashMap<>();
                        estudiante.put("nombre", rs.getString("Nombre_Estudiante"));
                        estudiantes.add(estudiante);
                    }
                }
            }
            return estudiantes;
        });
    }

    public void asignarProfesoresASeccion(List<String> nombresProfesores, String nombreSeccion) {
        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                for (String nombre : nombresProfesores) {
                    try (CallableStatement cs = conn.prepareCall(
                            "{call asignacion_seccion.Asignar_Seccion_Profesor(?, ?)}")) {
                        cs.setString(1, nombre);
                        cs.setString(2, nombreSeccion);
                        cs.execute();
                    }
                }
                conn.commit(); // Confirmar si todo va bien
            } catch (SQLException e) {
                conn.rollback(); // Revertir cambios si hay error
                throw e;
            }

            return null;
        });
    }

    public void asignarEstudiantesASeccion(List<String> nombresEstudiantes, String nombreSeccion) {
        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false); // Iniciar transacción

            try {
                for (String nombre : nombresEstudiantes) {
                    try (CallableStatement cs = conn.prepareCall(
                            "{call asignacion_seccion.Asignar_Seccion_Estudiante(?, ?)}")) {
                        cs.setString(1, nombre);
                        cs.setString(2, nombreSeccion);
                        cs.execute();
                    }
                }
                conn.commit(); // Confirmar si todo salió bien
            } catch (SQLException e) {
                conn.rollback(); // Revertir cambios si hubo error
                throw e;
            }

            return null;
        });
    }

    public List<Map<String, Object>> obtenerAsignacionesActuales(String nombreSeccion) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> asignaciones = new ArrayList<>();

            try (CallableStatement cs = conn.prepareCall(
                    "{ call Obtener_Asignaciones_Actuales(?, ?) }")) {

                cs.setString(1, nombreSeccion);
                cs.registerOutParameter(2, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        Map<String, Object> asignacion = new HashMap<>();
                        asignacion.put("Nombre_Profesor", rs.getString("Nombre_Profesor"));
                        asignacion.put("Nombre_Curso", rs.getString("Nombre_Curso"));
                        asignacion.put("Dia", rs.getString("Dia"));
                        asignacion.put("Hora", rs.getString("Hora"));
                        asignacion.put("ID_Horario", rs.getLong("ID_Horario"));
                        asignaciones.add(asignacion);
                    }
                }
            }
            return asignaciones;
        });
    }

    public void asignarMultiplesHorarios(List<String> asignacionesJson, String nombreSeccion) {
        ObjectMapper objectMapper = new ObjectMapper();

        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false);
            try {
                for (String jsonAsignacion : asignacionesJson) {
                    Map<String, Object> asignacion = objectMapper.readValue(jsonAsignacion, Map.class);

                    try (CallableStatement cs = conn.prepareCall(
                            "{ call Asignar_Horario_Profesor_Por_Nombre(?, ?, ?, ?) }")) {

                        cs.setString(1, (String) asignacion.get("profesor"));
                        cs.setString(2, (String) asignacion.get("curso"));
                        cs.setString(3, nombreSeccion);
                        cs.setLong(4, ((Number) asignacion.get("horario")).longValue());
                        cs.execute();
                    }
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Error en asignación múltiple: " + e.getMessage(), e);
            }
            return null;
        });
    }
}
