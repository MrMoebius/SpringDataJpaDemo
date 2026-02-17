package org.springdataapi.springdemojpa.controller;

import jakarta.validation.Valid;
import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.ClientesDTO;
import org.springdataapi.springdemojpa.security.CustomUserDetails;
import org.springdataapi.springdemojpa.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')") // [SPRING SECURITY] Solo ADMIN y EMPLEADO pueden listar clientes
    public ResponseEntity<List<Clientes>> listar() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Clientes> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @GetMapping("/mi-perfil")
    @PreAuthorize("hasRole('CLIENTE')") // [SPRING SECURITY] Solo CLIENTE puede ver su propio perfil
    public ResponseEntity<Clientes> miPerfil(Authentication authentication) {
        // [SPRING SECURITY] Obtiene los datos del usuario autenticado desde el contexto de seguridad
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(clienteService.findById(userDetails.getUserId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Map<String, String>> crear(@Valid @RequestBody ClientesDTO dto) {
        clienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Cliente creado correctamente"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Clientes> actualizar(@PathVariable Integer id, @Valid @RequestBody ClientesDTO dto) {
        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable Integer id) {
        clienteService.eliminar(id);
        return ResponseEntity.ok(Map.of("message", "Cliente eliminado correctamente"));
    }
}
