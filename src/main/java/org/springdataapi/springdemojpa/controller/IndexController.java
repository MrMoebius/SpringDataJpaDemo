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
            model.addAttribute("isAdmin", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
            model.addAttribute("isEmpleado", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_EMPLEADO")));
            model.addAttribute("isCliente", authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE")));
        }
        return "index";
    }
}
