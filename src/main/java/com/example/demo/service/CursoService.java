package com.example.demo.service;

import com.example.demo.entity.CursoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.dialect.OracleTypes;

@Service
public class CursoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

     public List<CursoEntity> obtenerTodosLosCursos() {
        return jdbcTemplate.execute((Connection con) -> {
            List<CursoEntity> cursos = new ArrayList<>();

            try (CallableStatement cs = con.prepareCall("{ call Mostrar_Todos_Los_Cursos(?) }")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        CursoEntity curso = new CursoEntity();
                        curso.setId(rs.getLong("ID_Curso"));
                        curso.setNombreCurso(rs.getString("Nombre_Curso"));
                        cursos.add(curso);
                    }
                }
            }
            return cursos;
        });
    }
}
