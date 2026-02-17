package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Productos;
import org.springdataapi.springdemojpa.models.ProductosDTO;
import org.springdataapi.springdemojpa.service.ProductosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/productos")
public class ProductosController {

    private final ProductosService productosService;

    public ProductosController(ProductosService productosService) {
        this.productosService = productosService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')") // [SPRING SECURITY] Todos los roles pueden leer productos
    public ResponseEntity<List<Productos>> listar() {
        return ResponseEntity.ok(productosService.findAll());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<List<Productos>> listarActivos() {
        return ResponseEntity.ok(productosService.findProductosActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<Productos> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(productosService.findById(id));
    }

    @GetMapping("/precio")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'CLIENTE')")
    public ResponseEntity<List<Productos>> listarPorPrecio(@RequestParam("valor") Double valor) {
        return ResponseEntity.ok(productosService.findByPrecio(valor));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // [SPRING SECURITY] Solo ADMIN y EMPLEADO pueden crear productos
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody ProductosDTO dto) {
        productosService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Producto creado correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Productos> actualizar(@PathVariable Integer id, @Valid @RequestBody ProductosDTO dto) {
        return ResponseEntity.ok(productosService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        productosService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Producto eliminado correctamente"));
    }
}
