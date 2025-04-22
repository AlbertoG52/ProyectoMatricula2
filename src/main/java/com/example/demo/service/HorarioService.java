/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.hibernate.dialect.OracleTypes;

/**
 *
 * @author guerr
 */
@Service
public class HorarioService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> obtenerTodosHorarios() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Map<String, Object>> horarios = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{ call PKG_LISTADOS_APP.Mostrar_Todos_Los_Horarios(?) }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();
                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Map<String, Object> horario = new HashMap<>();
                        horario.put("id", rs.getLong("ID_Horario"));
                        horario.put("dia", rs.getString("Nombre_Dia"));
                        horario.put("hora", rs.getString("Hora"));
                        horarios.add(horario);
                    }
                }
            }
            return horarios;
        });
    }
}
