package com.example.demo.service;

import com.example.demo.entity.PeriodoEntity;
import com.example.demo.entity.SeccionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.dialect.OracleTypes;

@Service
public class PeriodoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<PeriodoEntity> obtenerPeriodosMatricula() {
        return jdbcTemplate.execute((Connection con) -> {
            List<PeriodoEntity> periodos = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call mostrar_Matriculas(?) }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        PeriodoEntity periodo = new PeriodoEntity();
                        periodo.setSemestre(rs.getString("Semestre"));
                        periodo.setAnio(rs.getInt("Año"));
                        periodos.add(periodo);
                    }
                }
            }
            return periodos;
        });
    }

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

}
