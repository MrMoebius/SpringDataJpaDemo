package org.springdataapi.springdemojpa.controller.view;

import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springdataapi.springdemojpa.service.RolesEmpleadoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class ConsultasViewController {

    private final ClienteService clienteService;
    private final EmpleadosService empleadosService;
    private final ProductosService productosService;
    private final RolesEmpleadoService rolesEmpleadoService;

    public ConsultasViewController(ClienteService clienteService, EmpleadosService empleadosService,
                                   ProductosService productosService, RolesEmpleadoService rolesEmpleadoService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadosService;
        this.productosService = productosService;
        this.rolesEmpleadoService = rolesEmpleadoService;
    }

    @GetMapping("/consultas")
    public String principal() {
        return "consultas/principal";
    }

    @GetMapping("/consultas/clientes")
    public String clientes(@RequestParam(required = false) String telefono,
                           @RequestParam(required = false) String email,
                           @RequestParam(required = false) String tipoCliente,
                           @RequestParam(required = false) Integer idEmpleado,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
                           Model model) {
        model.addAttribute("empleados", empleadosService.findAll());
        String telNorm = norm(telefono), emailNorm = norm(email), tipoNorm = norm(tipoCliente);
        if (telNorm != null || emailNorm != null || tipoNorm != null || idEmpleado != null || fechaDesde != null) {
            model.addAttribute("clientes", clienteService.buscarClientesFiltrados(telNorm, emailNorm, tipoNorm, idEmpleado, fechaDesde));
        }
        model.addAttribute("telefono", telefono);
        model.addAttribute("email", email);
        model.addAttribute("tipoCliente", tipoCliente);
        model.addAttribute("idEmpleado", idEmpleado);
        model.addAttribute("fechaDesde", fechaDesde);
        return "consultas/clientes";
    }

    @GetMapping("/consultas/productos")
    public String productos(@RequestParam(required = false) String nombre,
                            @RequestParam(required = false) String categoria,
                            @RequestParam(required = false) Double precioMin,
                            @RequestParam(required = false) Double precioMax,
                            @RequestParam(required = false) String activo,
                            Model model) {
        String nomNorm = norm(nombre), catNorm = norm(categoria);
        Boolean activoBool = (activo != null && !activo.isBlank()) ? Boolean.parseBoolean(activo) : null;
        if (nomNorm != null || catNorm != null || precioMin != null || precioMax != null || activoBool != null) {
            model.addAttribute("productos", productosService.buscarProductosFiltrados(nomNorm, catNorm, precioMin, precioMax, activoBool));
        }
        model.addAttribute("nombre", nombre);
        model.addAttribute("categoria", categoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("activo", activo);
        return "consultas/productos";
    }

    @GetMapping("/consultas/empleados")
    public String empleados(@RequestParam(required = false) String telefono,
                            @RequestParam(required = false) String email,
                            @RequestParam(required = false) String estado,
                            @RequestParam(required = false) Integer idRol,
                            @RequestParam(required = false) String tieneClientes,
                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso,
                            Model model) {
        model.addAttribute("roles", rolesEmpleadoService.findAll());
        String telNorm = norm(telefono), emailNorm = norm(email), estNorm = norm(estado);
        Boolean tieneClientesBool = (tieneClientes != null && !tieneClientes.isBlank()) ? Boolean.parseBoolean(tieneClientes) : null;
        if (telNorm != null || emailNorm != null || estNorm != null || idRol != null || tieneClientesBool != null || fechaIngreso != null) {
            model.addAttribute("empleados", empleadosService.buscarEmpleadosFiltrados(telNorm, emailNorm, estNorm, idRol, tieneClientesBool, fechaIngreso));
        }
        model.addAttribute("telefono", telefono);
        model.addAttribute("email", email);
        model.addAttribute("estado", estado);
        model.addAttribute("idRol", idRol);
        model.addAttribute("tieneClientes", tieneClientes);
        model.addAttribute("fechaIngreso", fechaIngreso);
        return "consultas/empleados";
    }

    private String norm(String s) {
        return (s != null && !s.isBlank()) ? s.trim() : null;
    }
}
