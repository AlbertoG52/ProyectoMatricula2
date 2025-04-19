package com.example.demo.controller;

import com.example.demo.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }

    @PostMapping("/login")
    public String loginAdmin(
            @RequestParam String correo, 
            @RequestParam String contrasena, 
            Model model) {
        
        if (securityService.verificarAdmin(correo, contrasena)) {
            return "redirect:/dashboard"; // Redirige al dashboard después de login exitoso
        } else {
            model.addAttribute("error", "Credenciales inválidas");
            return "login"; // Muestra nuevamente el login con mensaje de error
        }
    }
}