/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.PeriodoEntity;
import com.example.demo.entity.SeccionEntity;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PeriodoService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author guerr
 */
@Controller
@RequestMapping("/matricula")
public class MatriculaController {

    @Autowired
    private PeriodoService periodoService;
    @Autowired
    private MatriculaService matriculaService;

    @GetMapping("/ver")
    public String verPeriodosDeMatricula(Model model) {
        List<PeriodoEntity> periodos = periodoService.obtenerPeriodosMatricula();
        model.addAttribute("periodos", periodos);
        return "ver-matricula";
    }

    @GetMapping("/verPeriodo")
    public String verSeccionesPorPeriodo(@RequestParam("semestre") String semestre,
            @RequestParam("anio") int anio,
            Model model) {
        List<SeccionEntity> secciones = matriculaService.obtenerSeccionesPorPeriodo(semestre, anio);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);
        model.addAttribute("secciones", secciones);
        return "ver-secciones-por-periodo";
    }

    @GetMapping("/verPeriodo/{id}")
    public String verHorarioSeccion(
            @PathVariable("id") Long id,
            @RequestParam("semestre") String semestre,
            @RequestParam("anio") int anio,
            Model model) {

        List<Map<String, Object>> horario = matriculaService.obtenerHorarioPorSeccion(id);

        // Pasar todos los parámetros al modelo
        model.addAttribute("horario", horario);
        model.addAttribute("idSeccion", id);  // Necesario para los botones
        model.addAttribute("semestre", semestre);  // Necesario para los botones
        model.addAttribute("anio", anio);  // Necesario para los botones

        return "ver-horario-seccion";
    }

    @GetMapping("/verEstudiantes/{id}")
    public String verEstudiantesSeccion(
            @PathVariable("id") Long idSeccion,
            @RequestParam("semestre") String semestre,
            @RequestParam("anio") int anio,
            Model model) {

        List<Map<String, Object>> estudiantes = matriculaService.obtenerEstudiantesPorSeccionPeriodo(idSeccion, semestre, anio);

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("idSeccion", idSeccion);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);

        return "estudiantes-seccion";
    }

    @GetMapping("/verProfesores/{id}")
    public String verProfesoresSeccion(
            @PathVariable("id") Long idSeccion,
            @RequestParam("semestre") String semestre,
            @RequestParam("anio") int anio,
            Model model) {

        List<Map<String, Object>> profesores = matriculaService.obtenerProfesoresPorSeccionPeriodo(idSeccion, semestre, anio);

        model.addAttribute("profesores", profesores);
        model.addAttribute("idSeccion", idSeccion);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);

        return "profesores-seccion";
    }

    // Mostrar el panel principal de matrícula
    @GetMapping
    public String mostrarPanelMatricula() {
        return "matricula";
    }

    // Mostrar vista para agregar matrícula
    @GetMapping("/agregar")
    public String mostrarVistaAgregarMatricula() {
        return "matricula-agregar";  // Vista con botones: Asignar Horarios / Asignar Estudiantes
    }

}
