package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.ClientesDTO;
import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.security.CustomUserDetails;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final EmpleadosService empleadosService;

    public ClienteController(ClienteService clienteService,
                             EmpleadosService empleadosService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadosService;
    }

    @GetMapping
    public String listar(Authentication authentication, Model model) {
        // Si es CLIENTE, redirige a su perfil
        if (authentication != null && 
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return "redirect:/clientes/mi-perfil";
        }
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/list";
    }

    @GetMapping("/mi-perfil")
    public String miPerfil(Authentication authentication, Model model) {
        if (authentication == null || 
            !authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            return "redirect:/clientes";
        }
        
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Clientes cliente = clienteService.findById(userDetails.getUserId());
        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    @GetMapping("/nuevo")
    public String nuevoCliente(Authentication authentication, Model model) {
        // Solo ADMIN y EMPLEADO pueden crear clientes
        if (authentication == null || 
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            return "redirect:/clientes";
        }
        model.addAttribute("clienteDTO", new ClientesDTO());
        model.addAttribute("empleados", empleadosService.findAll());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editarCliente(@PathVariable Integer id, Model model) {
        Clientes cliente = clienteService.findById(id);

        ClientesDTO dto = new ClientesDTO();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setTipo_cliente(cliente.getTipoCliente());
        dto.setPassword(cliente.getPassword());
        dto.setFecha_alta(cliente.getFechaAlta());
        if (cliente.getIdEmpleadoResponsable() != null) {
            dto.setId_empleadoresponsable(cliente.getIdEmpleadoResponsable().getId());
        }

        model.addAttribute("clienteDTO", dto);
        model.addAttribute("empleados", empleadosService.findAll());
        return "clientes/form";
    }

    @PostMapping("/guardar")
    public String guardarCliente(
            @Valid @ModelAttribute("clienteDTO") ClientesDTO dto,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("empleados", empleadosService.findAll());
            return "clientes/form";
        }

        try {
            if (dto.getId() == null) {
                clienteService.crear(dto);
            } else {
                clienteService.actualizar(dto.getId(), dto);
            }
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("empleados", empleadosService.findAll());
            return "clientes/form";
        }

        return "redirect:/clientes";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminarCliente(@PathVariable Integer id, Authentication authentication, Model model) {
        // Solo ADMIN y EMPLEADO pueden eliminar
        if (authentication == null || 
            authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENTE"))) {
            return "redirect:/clientes";
        }
        try {
            clienteService.eliminar(id);
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("clientes", clienteService.findAll());
            return "clientes/list";
        } catch (Exception e) {
            model.addAttribute("error", "No se puede eliminar el cliente porque tiene registros relacionados (facturas, presupuestos, etc.)");
            model.addAttribute("clientes", clienteService.findAll());
            return "clientes/list";
        }
        return "redirect:/clientes";
    }
}