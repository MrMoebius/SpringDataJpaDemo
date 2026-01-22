package org.springdataapi.springdemojpa.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            boolean isEmpleado = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLEADO"));
            boolean isCliente = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"));
            
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("isEmpleado", isEmpleado);
            model.addAttribute("isCliente", isCliente);
        }
        return "index";
    }
}
