package org.springdataapi.springdemojpa.controller.view;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.EmpleadosDTO;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.RolesEmpleadoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empleados")
@PreAuthorize("hasRole('ADMIN')")
public class EmpleadosViewController {

    private final EmpleadosService empleadosService;
    private final RolesEmpleadoService rolesEmpleadoService;

    public EmpleadosViewController(EmpleadosService empleadosService, RolesEmpleadoService rolesEmpleadoService) {
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
        if (emp.getIdRol() != null) dto.setIdRol(emp.getIdRol().getId());
        model.addAttribute("empleadosDTO", dto);
        model.addAttribute("roles", rolesEmpleadoService.findAll());
        return "empleados/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("empleadosDTO") EmpleadosDTO dto,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolesEmpleadoService.findAll());
            return "empleados/form";
        }
        try {
            if (dto.getId() == null) {
                empleadosService.crear(dto);
                redirectAttributes.addFlashAttribute("success", "Empleado añadido correctamente");
            } else {
                empleadosService.actualizar(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("success", "Empleado modificado correctamente");
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", rolesEmpleadoService.findAll());
            return "empleados/form";
        }
        return "redirect:/empleados";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            empleadosService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Empleado eliminado correctamente");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empleados", empleadosService.findAll());
            return "empleados/list";
        }
        return "redirect:/empleados";
    }
}
