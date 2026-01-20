package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.EmpleadosDTO;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.RolesEmpleadoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/empleados")
public class EmpleadosController {

    private final EmpleadosService empleadosService;
    private final RolesEmpleadoService rolesEmpleadoService;

    public EmpleadosController(EmpleadosService empleadosService,
                               RolesEmpleadoService rolesEmpleadoService) {
        this.empleadosService = empleadosService;
        this.rolesEmpleadoService = rolesEmpleadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empleados", empleadosService.findAll());
        return "empleados/list";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("empleadosDTO", new EmpleadosDTO());
        model.addAttribute("roles", rolesEmpleadoService.findAll());
        return "empleados/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model) {
        Empleados emp = empleadosService.findById(id);

        EmpleadosDTO dto = new EmpleadosDTO();
        dto.setId(emp.getId());
        dto.setNombre(emp.getNombre());
        dto.setEmail(emp.getEmail());
        dto.setTelefono(emp.getTelefono());
        dto.setEstado(emp.getEstado());
        dto.setFechaIngreso(emp.getFechaIngreso());
        if (emp.getIdRol() != null) {
            dto.setIdRol(emp.getIdRol().getId());
        }
        // NO rellenamos password para no exponerla

        model.addAttribute("empleadosDTO", dto);
        model.addAttribute("roles", rolesEmpleadoService.findAll());
        return "empleados/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("empleadosDTO") EmpleadosDTO dto,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolesEmpleadoService.findAll());
            return "empleados/form";
        }

        try {
            if (dto.getId() == null) {
                empleadosService.crear(dto);
            } else {
                empleadosService.actualizar(dto.getId(), dto);
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolesEmpleadoService.findAll());
            return "empleados/form";
        }

        return "redirect:/empleados";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id) {
        empleadosService.eliminar(id);
        return "redirect:/empleados";
    }
}
