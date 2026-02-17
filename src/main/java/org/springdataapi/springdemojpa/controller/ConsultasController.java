package org.springdataapi.springdemojpa.controller;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/consultas")
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
public class ConsultasController {

    private final ClienteService clienteService;
    private final EmpleadosService empleadosService;
    private final ProductosService productosService;

    public ConsultasController(ClienteService clienteService,
                               EmpleadosService empleadosService,
                               ProductosService productosService) {
        this.clienteService = clienteService;
        this.empleadosService = empleadosService;
        this.productosService = productosService;
    }

    @GetMapping("/clientes")
    public ResponseEntity<List<Clientes>> consultarClientes(
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String tipoCliente,
            @RequestParam(required = false) Integer idEmpleado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaDesde) {

        List<Clientes> clientes = clienteService.buscarClientesFiltrados(
                telefono, email, tipoCliente, idEmpleado, fechaDesde);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/productos")
    public ResponseEntity<List<Productos>> consultarProductos(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) Boolean activo) {

        List<Productos> productos = productosService.buscarProductosFiltrados(
                nombre, categoria, precioMin, precioMax, activo);
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/empleados")
    public ResponseEntity<List<Empleados>> consultarEmpleados(
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idRol,
            @RequestParam(required = false) Boolean tieneClientes,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaIngreso) {

        List<Empleados> empleados = empleadosService.buscarEmpleadosFiltrados(
                telefono, email, estado, idRol, tieneClientes, fechaIngreso);
        return ResponseEntity.ok(empleados);
    }
}
