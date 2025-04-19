package com.example.demo.controller;

import com.example.demo.entity.EncargadoEntity;
import com.example.demo.entity.EstudianteEntity;
import com.example.demo.service.EstudianteService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    // Método para obtener la lista de estudiantes
    @GetMapping("/estudiantes")
    public String obtenerListaEstudiantes(@RequestParam(required = false) String search, Model model) {
        // Si hay un parámetro de búsqueda, lo pasamos al servicio
        if (search != null && !search.isEmpty()) {
            model.addAttribute("estudiantes", estudianteService.buscarEstudiantePorNombre(search));
            model.addAttribute("searchQuery", search);  // Para mantener el valor de búsqueda en el formulario
            model.addAttribute("noResults", estudianteService.buscarEstudiantePorNombre(search).isEmpty());  // Para mostrar mensaje si no hay resultados
        } else {
            List<EstudianteEntity> estudiantes = estudianteService.obtenerTodosLosEstudiantes();
            model.addAttribute("estudiantes", estudiantes);
        }
        return "lista-estudiantes";
    }

    @GetMapping("/buscar-estudiante")
    public String buscarEstudiante(
            @RequestParam("search") String nombre,
            Model model) {

        // Llama al servicio que usa el procedimiento almacenado
        List<String> nombresEncontrados = estudianteService.buscarEstudiantePorNombre(nombre);

        // Convertimos los nombres en entidades mínimas para que el HTML pueda iterar igual
        List<EstudianteEntity> estudiantes = new ArrayList<>();
        for (String nombreEst : nombresEncontrados) {
            EstudianteEntity estudiante = new EstudianteEntity();
            estudiante.setNombreEstudiante(nombreEst);
            estudiantes.add(estudiante);
        }

        model.addAttribute("estudiantes", estudiantes);
        model.addAttribute("searchQuery", nombre); 

        return "buscar-estudiante"; // devuelve la vista buscar-estudiante.html
    }

    @GetMapping("/historial-academico-estudiante")
    public String obtenerHistorialAcademico(@RequestParam("nombreEstudiante") String nombreEstudiante, Model model) {
        EstudianteEntity estudiante = estudianteService.obtenerHistorialEstudiante(nombreEstudiante);

        if (estudiante != null && estudiante.getHistorialAcademico() != null && !estudiante.getHistorialAcademico().isEmpty()) {
            model.addAttribute("estudiante", estudiante);
        } else {
            model.addAttribute("mensaje", "No se encontró historial académico para el estudiante.");
        }

        return "historial-estudiante"; // Vista HTML
    }

// Mostrar formulario para crear un nuevo estudiante y encargado
    @GetMapping("/estudiantes/nuevo")
    public String mostrarFormularioEstudiante(Model model) {
        model.addAttribute("estudiante", new EstudianteEntity());
        model.addAttribute("encargado", new EncargadoEntity());
        return "nuevo-estudiante";  // Nombre del archivo HTML (vista)
    }

// Procesar la creación de un nuevo estudiante y encargado
    @PostMapping("/estudiantes/nuevo")
    public String crearEstudiante(@ModelAttribute EstudianteEntity estudiante,
            @ModelAttribute EncargadoEntity encargado) {
        estudianteService.crearEstudianteYEncargado(estudiante, encargado);
        return "redirect:/estudiantes";  // Redirige a la lista de estudiantes
    }

// Método para ver los detalles de un estudiante
    @GetMapping("/detalles")
    public String obtenerDetallesEstudiante(@RequestParam("nombreEstudiante") String nombreEstudiante, Model model) {
        // Llamamos al servicio para obtener la entidad Estudiante con sus encargados
        EstudianteEntity estudiante = estudianteService.obtenerEstudianteYEncargados(nombreEstudiante);

        if (estudiante != null) {
            // Pasamos la entidad EstudianteEntity al modelo
            model.addAttribute("estudiante", estudiante);
        } else {
            // Si no se encuentra al estudiante, mostramos un mensaje
            model.addAttribute("mensaje", "No se encontraron detalles para el estudiante.");
        }
        return "detalles-estudiante"; // Vista que muestra los detalles del estudiante
    }

    @GetMapping("/estudiantes/editar/{nombreEstudiante}")
    public String mostrarFormularioEdicion(@PathVariable("nombreEstudiante") String nombre, Model model) {
        EstudianteEntity estudiante = estudianteService.obtenerEstudianteYEncargados(nombre);
        EncargadoEntity encargado = estudiante.getEncargados().stream().findFirst().orElse(new EncargadoEntity());

        model.addAttribute("nombreOriginal", nombre);
        model.addAttribute("estudiante", estudiante);
        model.addAttribute("encargado", encargado);

        return "editar-estudiante";
    }

    @PostMapping("/estudiantes/editar")
    public String procesarFormularioEdicion(
            @RequestParam("nombreOriginal") String nombreOriginal,
            @ModelAttribute EstudianteEntity estudiante,
            @ModelAttribute EncargadoEntity encargado) {

        estudianteService.modificarEstudianteYEncargado(nombreOriginal, estudiante, encargado);
        return "redirect:/estudiantes";
    }

    @GetMapping("/detalles/eliminar")
    public String eliminarPorNombre(@RequestParam("nombreEstudiante") String nombre) {
        estudianteService.eliminarEstudiante(nombre);
        return "redirect:/estudiantes";
    }

}
