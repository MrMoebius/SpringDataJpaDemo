package org.springdataapi.springdemojpa.controller;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springdataapi.springdemojpa.service.RolesEmpleadoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ConsultasController
{

    private final RolesEmpleadoService rolesEmpleadoService;
    private ClienteService clienteService;
    private EmpleadosService empleadosService;
    private ProductosService productosService;

    public ConsultasController(ClienteService clienteService, EmpleadosService empleadoService, ProductosService productosService, RolesEmpleadoService rolesEmpleadoService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadoService;
        this.productosService = productosService;
        this.rolesEmpleadoService = rolesEmpleadoService;
    }




    @GetMapping("/consultas")
    public String consultaController()
    {
        return "consultas/principal";
    }


    @GetMapping("/consultas/clientes")
    public String consultarClientes(
            @RequestParam(name = "telefono", required = false) String telefono,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "tipoCliente", required = false) String tipoCliente,
            @RequestParam(name = "idEmpleado", required = false) Integer idEmpleado,
            @RequestParam(name = "fechaDesde", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde,
            Model model) {

        model.addAttribute("empleados", empleadosService.findAll());

        // Normalizar parámetros
        String telefonoNormalizado = (telefono != null && !telefono.trim().isEmpty()) ? telefono.trim() : null;
        String emailNormalizado = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        String tipoClienteNormalizado = (tipoCliente != null && !tipoCliente.trim().isEmpty()) ? tipoCliente.trim() : null;

        // Verificar si hay algún filtro activo
        boolean hayFiltros = telefonoNormalizado != null ||
                            emailNormalizado != null ||
                            tipoClienteNormalizado != null ||
                            idEmpleado != null ||
                            fechaDesde != null;

        if (hayFiltros) {
            List<Clientes> clientes = clienteService.buscarClientesFiltrados(
                    telefonoNormalizado, emailNormalizado, tipoClienteNormalizado, idEmpleado, fechaDesde);
            model.addAttribute("clientes", clientes);
        } else {
            // Si no hay filtros, no mostrar resultados
            model.addAttribute("clientes", null);
        }

        // Mantener valores en el formulario
        model.addAttribute("telefono", telefono);
        model.addAttribute("email", email);
        model.addAttribute("tipoCliente", tipoCliente);
        model.addAttribute("idEmpleado", idEmpleado);
        model.addAttribute("fechaDesde", fechaDesde);

        return "consultas/clientes";
    }

    @GetMapping("/consultas/productos")
    public String consultarProductos(
            @RequestParam(name = "nombre", required = false) String nombre,
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "precioMin", required = false) Double precioMin,
            @RequestParam(name = "precioMax", required = false) Double precioMax,
            @RequestParam(name = "activo", required = false) String activo,
            Model model) {

        // Normalizar parámetros
        String nombreNormalizado = (nombre != null && !nombre.trim().isEmpty()) ? nombre.trim() : null;
        String categoriaNormalizada = (categoria != null && !categoria.trim().isEmpty()) ? categoria.trim() : null;
        Boolean activoBool = null;
        if (activo != null && !activo.trim().isEmpty()) {
            activoBool = Boolean.parseBoolean(activo);
        }

        // Verificar si hay algún filtro activo
        boolean hayFiltros = nombreNormalizado != null ||
                            categoriaNormalizada != null ||
                            precioMin != null ||
                            precioMax != null ||
                            activoBool != null;

        if (hayFiltros) {
            List<Productos> productos = productosService.buscarProductosFiltrados(
                    nombreNormalizado, categoriaNormalizada, precioMin, precioMax, activoBool);
            model.addAttribute("productos", productos);
        } else {
            model.addAttribute("productos", null);
        }

        // Mantener valores en el formulario
        model.addAttribute("nombre", nombre);
        model.addAttribute("categoria", categoria);
        model.addAttribute("precioMin", precioMin);
        model.addAttribute("precioMax", precioMax);
        model.addAttribute("activo", activo);

        return "consultas/productos";
    }

    @GetMapping("/consultas/empleados")
    public String listarEmpleados(
            @RequestParam(name = "telefono", required = false) String telefono,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "estado", required = false) String estado,
            @RequestParam(name = "idRol", required = false) Integer idRol,
            @RequestParam(name = "tieneClientes", required = false) String tieneClientes,
            @RequestParam(name = "fechaIngreso", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso,
            Model model) {

        model.addAttribute("roles", rolesEmpleadoService.findAll());

        // Normalizar parámetros
        String telefonoNormalizado = (telefono != null && !telefono.trim().isEmpty()) ? telefono.trim() : null;
        String emailNormalizado = (email != null && !email.trim().isEmpty()) ? email.trim() : null;
        String estadoNormalizado = (estado != null && !estado.trim().isEmpty()) ? estado.trim() : null;
        Boolean tieneClientesBool = null;
        if (tieneClientes != null && !tieneClientes.trim().isEmpty()) {
            tieneClientesBool = Boolean.parseBoolean(tieneClientes);
        }

        // Verificar si hay algún filtro activo
        boolean hayFiltros = telefonoNormalizado != null ||
                            emailNormalizado != null ||
                            estadoNormalizado != null ||
                            idRol != null ||
                            tieneClientesBool != null ||
                            fechaIngreso != null;

        if (hayFiltros) {
            List<Empleados> empleados = empleadosService.buscarEmpleadosFiltrados(
                    telefonoNormalizado, emailNormalizado, estadoNormalizado, 
                    idRol, tieneClientesBool, fechaIngreso);
            model.addAttribute("empleados", empleados);
        } else {
            model.addAttribute("empleados", null);
        }

        // Mantener valores en el formulario
        model.addAttribute("telefono", telefono);
        model.addAttribute("email", email);
        model.addAttribute("estado", estado);
        model.addAttribute("idRol", idRol);
        model.addAttribute("tieneClientes", tieneClientes);
        model.addAttribute("fechaIngreso", fechaIngreso);

        return "consultas/empleados";
    }



}
