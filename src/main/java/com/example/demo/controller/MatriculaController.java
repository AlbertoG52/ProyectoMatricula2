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
