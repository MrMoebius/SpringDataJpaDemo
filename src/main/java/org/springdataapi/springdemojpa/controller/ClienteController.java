package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.ClienteDTO;
import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final EmpleadosService empleadosService;

    public ClienteController(ClienteService clienteService, EmpleadosService empleadosService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadosService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        return "clientes/list";
    }

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        model.addAttribute("clienteDTO", new ClienteDTO());
        model.addAttribute("empleados", empleadosService.findAll());
        return "clientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editarCliente(@PathVariable Integer id, Model model) {
        Clientes cliente = clienteService.findById(id);

        ClienteDTO dto = new ClienteDTO();
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
            @Valid @ModelAttribute("clienteDTO") ClienteDTO dto,
            BindingResult result,
            Model model
    ) {
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
    public String eliminarCliente(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return "redirect:/clientes";
    }
}