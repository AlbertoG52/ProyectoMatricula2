package com.example.demo.service;

import java.sql.CallableStatement;
import java.sql.Types;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean verificarAdmin(String correo, String contrasena) {
        String sql = "CALL verificar_admin(?, ?, ?)";
        
        try {
            int resultado = jdbcTemplate.execute(sql, (CallableStatement cs) -> {
                cs.setString(1, correo);
                cs.setString(2, contrasena);
                                cs.registerOutParameter(3, Types.INTEGER);
                cs.execute();
                return cs.getInt(3);
            });
            
            return resultado == 1;
        } catch (Exception e) {
            // Loggear el error si es necesario
            return false;
        }
    }
}