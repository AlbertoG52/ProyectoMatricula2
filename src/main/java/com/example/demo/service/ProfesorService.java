/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import com.example.demo.entity.ProfesorEntity;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.hibernate.dialect.OracleTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

/**
 *
 * @author guerr
 */
@Service
public class ProfesorService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall jdbcCall;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withCatalogName("PKG_PROFESOR_CRUD")
                .withProcedureName("Mostrar_Nombres_Profesores") // nombre del procedimiento dentro del paquete
                .withoutProcedureColumnMetaDataAccess() // evita que Spring intente adivinar metadata
                .declareParameters(
                        new SqlOutParameter("p_cursor", OracleTypes.CURSOR,
                                (rs, rowNum) -> {
                                    ProfesorEntity profesor = new ProfesorEntity();
                                    profesor.setNombreProfesor(rs.getString("Nombre_Profesor"));
                                    return profesor;
                                })
                );
    }

    // Método para mostrar todos los profesores
    public List<ProfesorEntity> obtenerTodosLosProfesores() {
        Map<String, Object> result = jdbcCall.execute();
        return (List<ProfesorEntity>) result.get("p_cursor");
    }

    // Buscar profesor por nombre
    public List<String> buscarProfesorPorNombre(String nombreEst) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call Buscar_Profesor_Por_Nombre(?, ?)}")) {
                cs.setString(1, nombreEst); // Parámetro IN
                cs.registerOutParameter(2, OracleTypes.CURSOR); // Parámetro OUT (cursor)
                cs.execute();

                List<String> nombres = new ArrayList<>();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        nombres.add(rs.getString("Nombre_Profesor"));
                    }
                }

                return nombres;

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    public List<Map<String, Object>> obtenerCursosYSeccionesPorProfesor(String nombreProfesor) {
        return jdbcTemplate.execute((Connection con) -> {
            List<Map<String, Object>> resultados = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{call Mostrar_Profesores_Secciones_Cursos(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        // Filtramos solo los resultados del profesor solicitado
                        if (rs.getString("Nombre_Profesor").equalsIgnoreCase(nombreProfesor)) {
                            Map<String, Object> item = new HashMap<>();
                            item.put("nombreProfesor", rs.getString("Nombre_Profesor"));
                            item.put("nombreCurso", rs.getString("Nombre_Curso"));
                            item.put("nombreSeccion", rs.getString("Nombre_Seccion"));
                            resultados.add(item);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return resultados;
        });
    }

    // Método para llamar al procedimiento almacenado y obtener los datos del estudiante y encargados    
    public ProfesorEntity obtenerInformacionProfesores(String nombreProfesor) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call PKG_PROFESOR_CRUD.Obtener_Info_Profesor(?, ?)}")) {
                cs.setString(1, nombreProfesor); // Parámetro IN
                cs.registerOutParameter(2, OracleTypes.CURSOR); // Parámetro OUT (cursor)
                cs.execute();

                ProfesorEntity profesor = null;

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        if (profesor == null) {
                            profesor = new ProfesorEntity();
                            profesor.setNombreProfesor(rs.getString("Nombre_Profesor"));
                            profesor.setCorreoProfesor(rs.getString("Correo"));
                            profesor.setProfesionProfesor(rs.getString("Profesion"));
                        }

                    }
                }
                return profesor;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // Crear profesor
    public ProfesorEntity crearProfesor(ProfesorEntity profesor) {
        // Ejecutamos el procedimiento almacenado
        jdbcTemplate.update("CALL PKG_PROFESOR_CRUD.Crear_Profesor(?, ?, ?)",
                profesor.getNombreProfesor(),
                profesor.getCorreoProfesor(),
                profesor.getProfesionProfesor());

        // Después de la inserción, retornamos el objeto ProfesorEntity
        return profesor;
    }

    // Modificar profesor
    public ProfesorEntity modificarProfesor(String nombreProfesorOriginal, ProfesorEntity profesor) {
        jdbcTemplate.update("CALL PKG_PROFESOR_CRUD.Modificar_Profesor(?, ?, ?, ?)",
                nombreProfesorOriginal,
                profesor.getNombreProfesor(),
                profesor.getCorreoProfesor(),
                profesor.getProfesionProfesor());

        return profesor;
    }

    // Eliminar profesor 
    public void eliminarProfesor(String nombreProfesor) {
        jdbcTemplate.update("CALL PKG_PROFESOR_CRUD.Eliminar_Profesor(?)", nombreProfesor);
    }

    public List<Map<String, Object>> obtenerProfesoresConProfesion() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> profesores = new ArrayList<>();

            try (CallableStatement cs = conn.prepareCall(
                    "{ call Mostrar_Nombres_Profesores_Con_Profesion(?) }")) {

                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Map<String, Object> profesor = new HashMap<>();
                        profesor.put("id", rs.getLong("ID_Profesor"));
                        profesor.put("nombre", rs.getString("Nombre_Profesor"));
                        profesor.put("profesion", rs.getString("Profesion"));
                        profesores.add(profesor);
                    }
                }
            }
            return profesores;
        });
    }

    public List<Map<String, Object>> obtenerProfesoresPorSeccionYPeriodo(String seccion, String semestre, int anio) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> profesores = new ArrayList<>();

            try (CallableStatement cs = conn.prepareCall(
                    "{ call Obtener_Profesores_Por_Seccion_Y_Periodo(?, ?, ?, ?) }")) {

                cs.setString(1, seccion);      // ej. "7-A"
                cs.setString(2, semestre);     // ej. "I"
                cs.setInt(3, anio);            // ej. 2025
                cs.registerOutParameter(4, OracleTypes.CURSOR);

                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(4)) {
                    while (rs.next()) {
                        Map<String, Object> profesor = new HashMap<>();
                        profesor.put("nombre", rs.getString("Nombre_Profesor"));
                        profesor.put("profesion", rs.getString("Profesion"));
                        profesores.add(profesor);
                    }
                }
            }

            return profesores;
        });
    }

}
