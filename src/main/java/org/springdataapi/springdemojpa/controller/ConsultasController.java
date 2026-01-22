package org.springdataapi.springdemojpa.controller;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springdataapi.springdemojpa.service.RolesEmpleadoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            @RequestParam(name = "categoria", required = false) String categoria,
            @RequestParam(name = "precioMin", required = false) Double precioMin,
            Model model) {

        try {
            List<Productos> productos;

            if ((categoria != null && !categoria.isEmpty()) || (precioMin != null && precioMin > 0)) {
                productos = productosService.BuscarProductosFiltrados(
                        categoria != null ? categoria : "",
                        precioMin != null ? precioMin : 0
                );
            } else {
                productos = productosService.findAll();
            }

            model.addAttribute("productos", productos);

            if (productos.isEmpty()) {
                model.addAttribute("mensaje", "No se encontraron productos con los filtros aplicados.");
            }

        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al consultar los productos: " + e.getMessage());
        }

        return "consultas/productos";
    }

    @GetMapping("/consultas/empleados")
    public String listarEmpleados(@RequestParam(value = "letra", required = false) String letra, Model model) {
        List<Empleados> empleados;
        if (letra != null && !letra.isEmpty()) {
            empleados = empleadosService.BuscarPorLetras(letra);
        } else {
            empleados = empleadosService.findAll();
        }
        model.addAttribute("empleados", empleados);
        model.addAttribute("letra", letra != null ? letra : "");
        return "consultas/empleados";
    }



}
