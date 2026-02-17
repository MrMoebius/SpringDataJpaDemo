package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.EmpleadosDTO;
import org.springdataapi.springdemojpa.service.EmpleadosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empleados")
@PreAuthorize("hasRole('ADMIN')") // [SPRING SECURITY] Solo ADMIN puede acceder a todos los endpoints de este controller
public class EmpleadosController {

    private final EmpleadosService empleadosService;

    public EmpleadosController(EmpleadosService empleadosService) {
        this.empleadosService = empleadosService;
    }

    @GetMapping
    public ResponseEntity<List<Empleados>> listar() {
        return ResponseEntity.ok(empleadosService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleados> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(empleadosService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody EmpleadosDTO dto) {
        empleadosService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Empleado creado correctamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleados> actualizar(@PathVariable Integer id, @Valid @RequestBody EmpleadosDTO dto) {
        return ResponseEntity.ok(empleadosService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        empleadosService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Empleado eliminado correctamente"));
    }
}
