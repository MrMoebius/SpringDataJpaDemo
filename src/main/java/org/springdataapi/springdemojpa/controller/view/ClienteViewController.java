package org.springdataapi.springdemojpa.controller.view;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.ClientesDTO;
import org.springdataapi.springdemojpa.security.CustomUserDetails;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clientes")
public class ClienteViewController {

    private final ClienteService clienteService;
    private final EmpleadosService empleadosService;

    public ClienteViewController(ClienteService clienteService, EmpleadosService empleadosService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadosService;
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        if (authentication != null &&
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            return "redirect:/clientes/mi-perfil";
        }
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/list";
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')")
    public String miPerfil(Authentication authentication, Model model) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        model.addAttribute("cliente", clienteService.findById(userDetails.getUserId()));
        return "clientes/perfil";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String nuevo(Model model) {
        model.addAttribute("clienteDTO", new ClientesDTO());
        model.addAttribute("empleados", empleadosService.findAll());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String editar(@PathVariable Integer id, Model model) {
        Clientes cliente = clienteService.findById(id);
        ClientesDTO dto = new ClientesDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setTipo_cliente(cliente.getTipoCliente());
        dto.setPassword(null);
        dto.setFecha_alta(cliente.getFechaAlta());
        if (cliente.getIdEmpleadoResponsable() != null) {
            dto.setId_empleadoresponsable(cliente.getIdEmpleadoResponsable().getId());
        }
        model.addAttribute("clienteDTO", dto);
        model.addAttribute("empleados", empleadosService.findAll());
        return "clientes/form";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String guardar(@Valid @ModelAttribute("clienteDTO") ClientesDTO dto,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", empleadosService.findAll());
            return "clientes/form";
        }
        try {
            if (dto.getId() == null) {
                clienteService.crear(dto);
                redirectAttributes.addFlashAttribute("success", "Cliente añadido correctamente");
            } else {
                clienteService.actualizar(dto.getId(), dto);
                redirectAttributes.addFlashAttribute("success", "Cliente modificado correctamente");
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empleados", empleadosService.findAll());
            return "clientes/form";
        }
        return "redirect:/clientes";
    }

    @GetMapping("/{id}/eliminar")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public String eliminar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            clienteService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Cliente eliminado correctamente");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clientes", clienteService.findAll());
            return "clientes/list";
        }
        return "redirect:/clientes";
    }
}
