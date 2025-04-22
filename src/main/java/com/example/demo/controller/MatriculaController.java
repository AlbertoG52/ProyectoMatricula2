/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.CursoEntity;
import com.example.demo.entity.PeriodoEntity;
import com.example.demo.entity.SeccionEntity;
import com.example.demo.entity.ProfesorEntity;
import com.example.demo.service.CursoService;
import com.example.demo.service.HorarioService;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PeriodoService;
import com.example.demo.service.ProfesorService;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    private ProfesorService profesorService;

    @Autowired
    private HorarioService horarioService;

    private final CursoService cursoService;

    public MatriculaController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

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

    @GetMapping("/elegir-matricula")
    public String elegirPeriodosDeMatricula(Model model) {
        List<PeriodoEntity> periodos = periodoService.obtenerPeriodosMatricula();
        model.addAttribute("periodos", periodos);
        return "elegir-matricula";
    }

    @GetMapping("/desplegar-estudiantes-no-matriculados")
    public String mostrarEstudiantesNoMatriculados(
            @RequestParam String semestre,
            @RequestParam int anio,
            @RequestParam(required = false) Boolean showContinue,
            Model model) {

        List<Map<String, Object>> estudiantes
                = matriculaService.obtenerEstudiantesNoMatriculados(semestre, anio);

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);

        if (showContinue != null && showContinue) {
            model.addAttribute("showContinue", true);
        }

        return "desplegar-estudiantes-no-matriculados";
    }

    @PostMapping("/matricular-estudiantes")
    public String matricularEstudiantes(
            @RequestParam String semestre,
            @RequestParam int anio,
            @RequestParam(value = "estudiantesSeleccionados", required = false) List<String> estudiantesSeleccionados,
            RedirectAttributes redirectAttributes) {

        // Validación básica
        if (estudiantesSeleccionados == null || estudiantesSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un estudiante");
            return "redirect:/matricula/desplegar-estudiantes-no-matriculados?semestre=" + semestre + "&anio=" + anio;
        }

        try {
            matriculaService.matricularEstudiantes(estudiantesSeleccionados, semestre, anio);
            redirectAttributes.addFlashAttribute("success",
                    "Matrículas completadas: " + estudiantesSeleccionados.size() + " estudiantes");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error en matrícula: " + e.getMessage());
        }

        return "redirect:/matricula/desplegar-estudiantes-no-matriculados?semestre=" + semestre + "&anio=" + anio;
    }

    // Mostrar vista para agregar matrícula
    @GetMapping("/agregar")
    public String mostrarVistaAgregarMatricula(
            @RequestParam String semestre,
            @RequestParam int anio,
            Model model) {
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);
        return "matricula-agregar";
    }

    @GetMapping("/desplegar-todas-secciones")
    public String mostrarTodasSecciones(
            @RequestParam String semestre,
            @RequestParam int anio,
            Model model) {

        List<Map<String, Object>> secciones = matriculaService.obtenerTodasLasSecciones();

        model.addAttribute("secciones", secciones);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);

        return "desplegar-todas-secciones";
    }

    @GetMapping("/asignar-seccion")
    public String asignarSeccion(
            @RequestParam String nombreSeccion, // Nuevo parámetro
            @RequestParam String semestre,
            @RequestParam int anio,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("nombreSeccion", nombreSeccion);  // Nuevo atributo
        redirectAttributes.addAttribute("semestre", semestre);
        redirectAttributes.addAttribute("anio", anio);

        return "redirect:/matricula/gestion-seccion";
    }

    @GetMapping("/gestion-seccion")
    public String gestionSeccion(
            @RequestParam String nombreSeccion,
            @RequestParam String semestre,
            @RequestParam int anio,
            Model model) {

        // Obtener estudiantes matriculados en este periodo
        List<Map<String, Object>> estudiantes = matriculaService.obtenerEstudiantesConMatricula(semestre, anio);

        // Obtener todos los profesores con profesión
        List<Map<String, Object>> profesores = profesorService.obtenerProfesoresConProfesion();

        model.addAttribute("nombreSeccion", nombreSeccion);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);
        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("profesores", profesores);

        return "gestion-seccion";
    }

    @PostMapping("/asignar-profesores")
    public String asignarProfesores(
            @RequestParam String nombreSeccion,
            @RequestParam String semestre,
            @RequestParam int anio,
            @RequestParam(value = "profesoresSeleccionados", required = false) List<String> profesoresSeleccionados,
            RedirectAttributes redirectAttributes) {

        if (profesoresSeleccionados == null || profesoresSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un profesor");
        } else {
            try {
                matriculaService.asignarProfesoresASeccion(profesoresSeleccionados, nombreSeccion);
                redirectAttributes.addFlashAttribute("success",
                        "Profesores asignados: " + profesoresSeleccionados.size());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error",
                        "Error al asignar profesores: " + e.getMessage());
            }
        }

        redirectAttributes.addAttribute("nombreSeccion", nombreSeccion);
        redirectAttributes.addAttribute("semestre", semestre);
        redirectAttributes.addAttribute("anio", anio);

        return "redirect:/matricula/gestion-seccion";
    }

    @PostMapping("/asignar-estudiantes")
    public String asignarEstudiantes(
            @RequestParam String nombreSeccion,
            @RequestParam String semestre,
            @RequestParam int anio,
            @RequestParam(value = "estudiantesSeleccionados", required = false) List<String> estudiantesSeleccionados,
            RedirectAttributes redirectAttributes) {

        if (estudiantesSeleccionados == null || estudiantesSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un estudiante");
        } else {
            try {
                matriculaService.asignarEstudiantesASeccion(estudiantesSeleccionados, nombreSeccion);
                redirectAttributes.addFlashAttribute("success",
                        "Estudiantes asignados: " + estudiantesSeleccionados.size());
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error",
                        "Error al asignar estudiantes: " + e.getMessage());
            }
        }

        redirectAttributes.addAttribute("nombreSeccion", nombreSeccion);
        redirectAttributes.addAttribute("semestre", semestre);
        redirectAttributes.addAttribute("anio", anio);

        return "redirect:/matricula/gestion-seccion";
    }


    @GetMapping("/gestion-horario")
    public String gestionHorario(
            @RequestParam String nombreSeccion,
            @RequestParam String semestre,
            @RequestParam int anio,
            Model model) {

        // Obtener datos necesarios
        List<Map<String, Object>> profesores = profesorService.obtenerProfesoresPorSeccionYPeriodo(nombreSeccion, semestre, anio);
        List<CursoEntity> cursos = cursoService.obtenerTodosLosCursos();
        List<Map<String, Object>> horarios = horarioService.obtenerTodosHorarios();
        List<Map<String, Object>> asignacionesActuales = matriculaService.obtenerAsignacionesActuales(nombreSeccion);

        model.addAttribute("nombreSeccion", nombreSeccion);
        model.addAttribute("semestre", semestre);
        model.addAttribute("anio", anio);
        model.addAttribute("profesores", profesores);
        model.addAttribute("cursos", cursos);
        model.addAttribute("horarios", horarios);
        model.addAttribute("asignaciones", asignacionesActuales);

        return "gestion-horario";
    }

    @PostMapping("/asignar-horarios")
    public String asignarHorarios(
            @RequestParam String nombreSeccion,
            @RequestParam String semestre,
            @RequestParam int anio,
            @RequestParam(value = "asignaciones", required = false) List<String> asignacionesJson,
            RedirectAttributes redirectAttributes) {

        if (asignacionesJson == null || asignacionesJson.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe agregar al menos una asignación de horario");
        } else {
            try {
                matriculaService.asignarMultiplesHorarios(asignacionesJson, nombreSeccion);
                redirectAttributes.addFlashAttribute("success", asignacionesJson.size() + " horarios asignados correctamente");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Error al asignar horarios: " + e.getMessage());
            }
        }

        return "redirect:/matricula/gestion-horario?nombreSeccion=" + URLEncoder.encode(nombreSeccion, StandardCharsets.UTF_8)
                + "&semestre=" + semestre
                + "&anio=" + anio;
    }
}
