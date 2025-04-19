package com.example.demo.service;

import com.example.demo.entity.EncargadoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.example.demo.entity.EstudianteEntity;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource;
import org.hibernate.dialect.OracleTypes;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;

@Service
public class EstudianteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall jdbcCall;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcCall = new SimpleJdbcCall(dataSource)
                .withProcedureName("Mostrar_Nombres_Estudiantes") // Nombre del procedimiento
                .returningResultSet("p_cursor", (rs, rowNum) -> { // Asegúrate de que el nombre del cursor sea el correcto
                    EstudianteEntity estudiante = new EstudianteEntity();
                    estudiante.setNombreEstudiante(rs.getString("Nombre_Estudiante")); // Solo asignamos el nombre
                    return estudiante;
                });
    }

    // Método para mostrar todos los estudiantes
    public List<EstudianteEntity> obtenerTodosLosEstudiantes() {
        Map<String, Object> result = jdbcCall.execute();  // Ejecuta el procedimiento
        List<EstudianteEntity> estudiantes = (List<EstudianteEntity>) result.get("p_cursor");  // Obtiene la lista del cursor
        return estudiantes != null ? estudiantes : new ArrayList<>();  // Devuelve la lista de estudiantes o una lista vacía
    }

    // Crear estudiante y encargado
    public EstudianteEntity crearEstudianteYEncargado(EstudianteEntity estudiante, EncargadoEntity encargado) {
        // Ejecutamos el procedimiento almacenado
        jdbcTemplate.update("CALL pkg_estudiantes.Crear_Estudiante_Y_Encargado(?, ?, ?, ?, ?, ?)",
                estudiante.getNombreEstudiante(),
                estudiante.getTelefonoEstudiante(),
                estudiante.getCorreoEstudiante(),
                encargado.getNombreEncargado(),
                encargado.getTelefonoEncargado(),
                encargado.getCorreoEncargado());

        // Después de la inserción, retornamos el objeto EstudianteEntity
        return estudiante;
    }

    // Modificar estudiante y encargado (recomendado: por ID del estudiante)
    public EstudianteEntity modificarEstudianteYEncargado(String nombreEstOriginal, EstudianteEntity estudiante, EncargadoEntity encargado) {
        jdbcTemplate.update("CALL pkg_estudiantes.Modificar_Estudiante_Y_Encargado(?, ?, ?, ?, ?, ?, ?)",
                nombreEstOriginal,
                estudiante.getNombreEstudiante(),
                estudiante.getTelefonoEstudiante(),
                estudiante.getCorreoEstudiante(),
                encargado.getNombreEncargado(),
                encargado.getTelefonoEncargado(),
                encargado.getCorreoEncargado());

        return estudiante;
    }
    

    // Eliminar estudiante 
    public void eliminarEstudiante(String nombreEstudiante) {
        jdbcTemplate.update("CALL pkg_estudiantes.Eliminar_Estudiante(?)", nombreEstudiante);
    }

    // Buscar estudiante por nombre
    public List<String> buscarEstudiantePorNombre(String nombreEst) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call Buscar_Estudiante_Por_Nombre(?, ?)}")) {
                cs.setString(1, nombreEst); // Parámetro IN
                cs.registerOutParameter(2, OracleTypes.CURSOR); // Parámetro OUT (cursor)
                cs.execute();

                List<String> nombres = new ArrayList<>();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        nombres.add(rs.getString("Nombre_Estudiante"));
                    }
                }

                return nombres;

            } catch (SQLException e) {
                e.printStackTrace();
                return Collections.emptyList();
            }
        });
    }

    // Obtener historial del estudiante
    public EstudianteEntity obtenerHistorialEstudiante(String nombreEstudiante) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call Obtener_Info_Completa_Estudiante(?, ?)}")) {
                cs.setString(1, nombreEstudiante); // Parámetro IN
                cs.registerOutParameter(2, OracleTypes.CURSOR); // Parámetro OUT (cursor)
                cs.execute();

                EstudianteEntity estudiante = new EstudianteEntity();
                estudiante.setNombreEstudiante(nombreEstudiante); // Ya lo conocemos

                List<Map<String, String>> historial = new ArrayList<>();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        Map<String, String> cursoInfo = new HashMap<>();
                        cursoInfo.put("Nombre_Curso", rs.getString("Nombre_Curso"));
                        cursoInfo.put("Estado", rs.getString("Estado"));
                        cursoInfo.put("Semestre", rs.getString("Semestre"));
                        cursoInfo.put("Año", rs.getString("Año"));
                        historial.add(cursoInfo);
                    }
                }

                // Puedes crear un setter personalizado si quieres encapsular mejor
                estudiante.setHistorialAcademico(historial);

                return estudiante;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // Método para llamar al procedimiento almacenado y obtener los datos del estudiante y encargados    
    public EstudianteEntity obtenerEstudianteYEncargados(String nombreEstudiante) {
        return jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{call Obtener_Estudiante_Encargados(?, ?)}")) {
                cs.setString(1, nombreEstudiante); // Parámetro IN
                cs.registerOutParameter(2, OracleTypes.CURSOR); // Parámetro OUT (cursor)
                cs.execute();

                EstudianteEntity estudiante = null;
                Set<EncargadoEntity> encargados = new HashSet<>();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        if (estudiante == null) {
                            estudiante = new EstudianteEntity();
                            estudiante.setNombreEstudiante(rs.getString("Nombre_Estudiante"));
                            estudiante.setTelefonoEstudiante(rs.getString("Telefono_Estudiante"));
                            estudiante.setCorreoEstudiante(rs.getString("Correo_Estudiante"));
                        }

                        EncargadoEntity encargado = new EncargadoEntity();
                        encargado.setNombreEncargado(rs.getString("Nombre_Encargado"));
                        encargado.setTelefonoEncargado(rs.getString("Telefono_Encargado"));
                        encargado.setCorreoEncargado(rs.getString("Correo_Encargado"));

                        encargados.add(encargado);
                    }
                }

                if (estudiante != null) {
                    estudiante.setEncargados(encargados);
                }

                return estudiante;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

}
