/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.GeneracionEntity;
import com.example.demo.entity.GrupoEntity;
import com.example.demo.entity.SeccionEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.dialect.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
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
                } catch (SQLException e) {
                    System.err.println("Error al obtener secciones: " + e.getMessage());
                    throw new DataAccessException("Error en base de datos al obtener secciones", e) {
                    };
                }
                return secciones;
            });
        } catch (DataAccessException e) {
            System.err.println("Error en obtenerTodasLasSecciones: " + e.getMessage());
            return Collections.emptyList();
        }
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

// Modificación en el Service
    public List<Map<String, Object>> obtenerAsignacionesPorPeriodo(String semestre, int anio) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> asignaciones = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{ call Obtener_Asignaciones_Por_Periodo(?, ?, ?) }")) {
                cs.setString(1, semestre);
                cs.setInt(2, anio);
                cs.registerOutParameter(3, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        Map<String, Object> asignacion = new HashMap<>();
                        asignacion.put("idHorario", rs.getLong("ID_HORARIO"));
                        asignacion.put("nombreSeccion", rs.getString("NOMBRE_SECCION"));
                        asignacion.put("nombreProfesor", rs.getString("NOMBRE_PROFESOR"));
                        asignacion.put("nombreCurso", rs.getString("NOMBRE_CURSO"));
                        asignacion.put("dia", rs.getString("DIA"));
                        asignacion.put("hora", rs.getString("HORA"));

                        asignaciones.add(asignacion);
                    }
                }
            }
            return asignaciones;
        });
    }

    public void agregarAsignacionHorario(String nombreProfesor, String nombreCurso, Long idHorario, String nombreSeccion) {
        try {
            jdbcTemplate.update(
                    "{call Asignar_Horario_Profesor_Por_Nombre(?, ?, ?, ?)}",
                    nombreProfesor,
                    nombreCurso,
                    nombreSeccion,
                    idHorario
            );
        } catch (DataAccessException e) {
            // Extraer el mensaje de error de Oracle si está disponible
            String errorMessage = "Error al asignar horario";
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                errorMessage = sqlEx.getMessage();

                // Manejar errores específicos de Oracle
                if (sqlEx.getErrorCode() >= 20000 && sqlEx.getErrorCode() <= 20999) {
                    // Estos son los errores personalizados de tu procedimiento
                    errorMessage = sqlEx.getMessage().replace("ORA-200XX: ", "");
                }
            }
            throw new RuntimeException(errorMessage, e);
        }
    }

    public void eliminarAsignacionHorario(Long idHorario, String nombreSeccion) {
        jdbcTemplate.execute((Connection conn) -> {
            // Primero obtener el ID_Seccion
            Long idSeccion = jdbcTemplate.queryForObject(
                    "SELECT s.ID_Seccion FROM Seccion s "
                    + "JOIN Generacion g ON s.ID_Generacion = g.ID_Generacion "
                    + "JOIN Grupo gr ON s.ID_Grupo = gr.ID_Grupo "
                    + "WHERE g.Generacion || '-' || gr.Grupo = ?",
                    Long.class, nombreSeccion);

            // Verificar si hay matrículas dependientes
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM Matricula_Curso_Estudiantes mce "
                    + "JOIN Curso_Seccion_Horario csh ON mce.ID_Curso = csh.ID_Curso "
                    + "WHERE csh.ID_Horario = ? AND csh.ID_Seccion = ?",
                    Integer.class, idHorario, idSeccion);

            if (count > 0) {
                throw new DataIntegrityViolationException(
                        "No se puede eliminar el horario porque tiene matrículas asociadas");
            }

            // Si no hay dependencias, proceder con la eliminación
            jdbcTemplate.update(
                    "DELETE FROM Curso_Seccion_Horario WHERE ID_Horario = ? AND ID_Seccion = ?",
                    idHorario, idSeccion);
            return null;
        });
    }

    public void eliminarAsignacionesHorario(List<Long> idsHorarios) {
        jdbcTemplate.execute((Connection conn) -> {
            conn.setAutoCommit(false);
            try {
                for (Long id : idsHorarios) {
                    try (PreparedStatement ps = conn.prepareStatement(
                            "DELETE FROM Curso_Seccion_Horario WHERE ID_Horario = ?")) {
                        ps.setLong(1, id);
                        ps.executeUpdate();
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
            return null;
        });
    }

    public void eliminarProgramacionSeccion(Long idSeccion, String semestre, int anio) {
        jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{ call Eliminar_Programacion_Seccion_Periodo(?, ?, ?) }")) {
                cs.setLong(1, idSeccion);
                cs.setString(2, semestre);
                cs.setInt(3, anio);
                cs.execute();
            } catch (SQLException e) {
                throw new DataAccessException("Error al ejecutar Eliminar_Programacion_Seccion_Periodo", e) {
                };
            }
            return null;
        });
    }

}
