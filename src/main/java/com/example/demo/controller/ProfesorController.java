/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.demo.controller;

import com.example.demo.entity.CursoEntity;
import com.example.demo.entity.ProfesorEntity;
import com.example.demo.service.CursoService;
import com.example.demo.service.ProfesorService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author guerr
 */
@Controller
public class ProfesorController {

    @Autowired
    private ProfesorService profesorService;

    private final CursoService cursoService;

    public ProfesorController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // Método para obtener la lista de estudiantes
    @GetMapping("/profesores")
    public String obtenerListaProfesores(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isEmpty()) {
            List<String> profesores = profesorService.buscarProfesorPorNombre(search);
            model.addAttribute("profesores", profesores);
            model.addAttribute("searchQuery", search);
            model.addAttribute("noResults", profesores.isEmpty());
        } else {
            List<ProfesorEntity> profesores = profesorService.obtenerTodosLosProfesores();
            model.addAttribute("profesores", profesores);
        }
        return "lista-profesores";
    }

    @GetMapping("/buscar-profesor")
    public String buscarProfesor(
            @RequestParam("search") String nombre,
            Model model) {

        // Llama al servicio que usa el procedimiento almacenado
        List<String> nombresEncontrados = profesorService.buscarProfesorPorNombre(nombre);

        // Convertimos los nombres en entidades mínimas para que el HTML pueda iterar igual
        List<ProfesorEntity> profesores = new ArrayList<>();
        for (String nombreProfesor : nombresEncontrados) {
            ProfesorEntity profesor = new ProfesorEntity();
            profesor.setNombreProfesor(nombreProfesor);
            profesores.add(profesor);
        }

        model.addAttribute("profesores", profesores);
        model.addAttribute("searchQuery", nombre);

        return "buscar-profesores"; // devuelve la vista buscar-estudiante.html
    }

    @GetMapping("/informacion-profesor")
    public String obtenerDetallesProfesor(@RequestParam("nombreProfesor") String nombreProfesor, Model model) {
        // Llamamos al servicio para obtener la entidad Estudiante con sus encargados
        ProfesorEntity profesor = profesorService.obtenerInformacionProfesores(nombreProfesor);

        if (profesor != null) {
            // Pasamos la entidad EstudianteEntity al modelo
            model.addAttribute("profesor", profesor);
        } else {
            // Si no se encuentra al profesor, mostramos un mensaje
            model.addAttribute("mensaje", "No se encontraron detalles para el profesor.");
        }
        return "detalles-profesor"; // Vista que muestra los detalles del estudiante
    }

    // Mostrar formulario para crear un nuevo profesor
    @GetMapping("/profesores/nuevo")
    public String mostrarFormularioProfesor(Model model) {
        model.addAttribute("profesor", new ProfesorEntity());

        List<CursoEntity> cursos = cursoService.obtenerTodosLosCursos();
        model.addAttribute("cursos", cursos);

        return "nuevo-profesor";
    }

// Procesar la creación de un nuevo profesor
    @PostMapping("/profesores/nuevo")
    public String crearProfesor(@ModelAttribute ProfesorEntity profesor) {
        profesorService.crearProfesor(profesor);
        return "redirect:/profesores";  // Redirige a la lista de profesores
    }

    @GetMapping("/profesores/editar/{nombreProfesor}")
    public String mostrarFormularioEdicion(@PathVariable("nombreProfesor") String nombre, Model model) {
        ProfesorEntity profesor = profesorService.obtenerInformacionProfesores(nombre);

        model.addAttribute("nombreOriginal", nombre);
        model.addAttribute("profesor", profesor);

        // ✅ Agrega los cursos para que aparezcan en el <select>
        List<CursoEntity> cursos = cursoService.obtenerTodosLosCursos();
        model.addAttribute("cursos", cursos);

        return "editar-profesor";
    }

    @PostMapping("/profesores/editar")
    public String procesarFormularioEdicion(
            @RequestParam("nombreOriginal") String nombreOriginal,
            @ModelAttribute ProfesorEntity profesor) {

        profesorService.modificarProfesor(nombreOriginal, profesor);
        return "redirect:/profesores";
    }

    @GetMapping("/informacion-profesor/eliminar")
    public String eliminarPorNombre(@RequestParam("nombreProfesor") String nombre) {
        profesorService.eliminarProfesor(nombre);
        return "redirect:/profesores";
    }
}
