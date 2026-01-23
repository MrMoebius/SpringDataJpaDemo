package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.EmpleadosDTO;
import org.springdataapi.springdemojpa.models.RolesEmpleado;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.repository.RolesEmpleadoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadosService {

    private final EmpleadosRepository empleadosRepository;
    private final RolesEmpleadoRepository rolesEmpleadoRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository,
                            RolesEmpleadoRepository rolesEmpleadoRepository) {
        this.empleadosRepository = empleadosRepository;
        this.rolesEmpleadoRepository = rolesEmpleadoRepository;
    }

    public List<Empleados> findAll() {
        return empleadosRepository.findAll();
    }

    public Empleados findById(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        return empleadosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    // Mantengo tu firma, pero nota: aquí se lanza excepción si no existe.
    public Optional<Empleados> findByEmail(String email) {
        if (email == null || email.isBlank()) throw new RuntimeException("Email obligatorio");
        String e = email.trim();
        return Optional.ofNullable(empleadosRepository.findByEmail(e)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado por email")));
    }

    // Mantengo tu firma, pero nota: aquí se lanza excepción si no existe.
    public Optional<Empleados> findByTelefono(String telefono) {
        String tel = normalizarYValidarTelefono(telefono);
        if (tel == null) throw new RuntimeException("Teléfono obligatorio");

        return Optional.ofNullable(empleadosRepository.findByTelefono(tel)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado por teléfono")));
    }

    public List<Empleados> findByEstado(String estado) {
        if (estado == null || estado.isBlank()) throw new RuntimeException("Estado obligatorio");
        return empleadosRepository.findByEstado(estado.trim());
    }

    public List<Empleados> findByFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso == null) throw new RuntimeException("Fecha de ingreso obligatoria");
        return empleadosRepository.findByFechaIngreso(fechaIngreso);
    }

    public void crear(EmpleadosDTO dto) {
        validarCamposCrear(dto);

        String email = dto.getEmail().trim();
        String telefono = dto.getTelefono(); // ya viene normalizado y validado en validarCamposCrear()

        if (empleadosRepository.existsByEmail(email)) {
            throw new RuntimeException("Email ya registrado");
        }
        if (empleadosRepository.existsByTelefono(telefono)) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        RolesEmpleado rol = rolesEmpleadoRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Empleados e = new Empleados();
        e.setNombre(dto.getNombre().trim());
        e.setEmail(email);
        e.setTelefono(telefono);
        e.setPassword(dto.getPassword()); // sin hash (igual que tu código actual)
        e.setIdRol(rol);

        e.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
        e.setEstado(normalizarEstado(dto.getEstado()));

        empleadosRepository.save(e);
    }

    public void eliminar(Integer id) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        if (!empleadosRepository.existsById(id)) {
            throw new RuntimeException("Empleado no existe");
        }
        try {
            empleadosRepository.deleteById(id);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("No se puede eliminar el empleado porque tiene registros relacionados (clientes asignados, facturas, etc.)");
        }
    }

    public Empleados actualizar(Integer id, EmpleadosDTO dto) {
        if (id == null) throw new RuntimeException("Id obligatorio");

        Empleados e = empleadosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        validarCamposActualizar(dto);

        String nuevoEmail = dto.getEmail().trim();
        String nuevoTelefono = dto.getTelefono(); // ya viene normalizado (o null) por validarCamposActualizar()

        if (!e.getEmail().equals(nuevoEmail) && empleadosRepository.existsByEmail(nuevoEmail)) {
            throw new RuntimeException("Email ya registrado");
        }

        if (nuevoTelefono != null
                && (e.getTelefono() == null || !nuevoTelefono.equals(e.getTelefono()))
                && empleadosRepository.existsByTelefono(nuevoTelefono)) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        e.setNombre(dto.getNombre().trim());
        e.setEmail(nuevoEmail);
        e.setTelefono(nuevoTelefono);

        if (dto.getFechaIngreso() != null) {
            e.setFechaIngreso(dto.getFechaIngreso());
        }

        if (dto.getEstado() != null) {
            e.setEstado(normalizarEstado(dto.getEstado()));
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            e.setPassword(dto.getPassword());
        }

        if (dto.getIdRol() != null) {
            RolesEmpleado rol = rolesEmpleadoRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            e.setIdRol(rol);
        }

        return empleadosRepository.save(e);
    }

    public List<Empleados> BuscarPorLetras(String letra) {
        return empleadosRepository.buscarPorLetra(letra);
    }

    public List<Empleados> buscarEmpleadosFiltrados(
            String telefono, String email, String estado,
            Integer idRol, Boolean tieneClientes, LocalDate fechaIngreso) {
        return empleadosRepository.buscarEmpleadosFiltrados(
                telefono, email, estado, idRol, tieneClientes, fechaIngreso);
    }

    // ==========================
    // VALIDACIONES (estilo ClienteService)
    // ==========================

    private void validarCamposCrear(EmpleadosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new RuntimeException("La password pa otro día ¿no?");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() || !dto.getEmail().contains("@")) {
            throw new RuntimeException("Email obligatorio");
        }

        if (dto.getIdRol() == null) {
            throw new RuntimeException("Rol obligatorio");
        }

        String tel = normalizarYValidarTelefono(dto.getTelefono());
        if (tel == null) {
            throw new RuntimeException("¿Y tú teléfono olvidona?");
        }
        // Requiere que EmpleadosDTO tenga setTelefono(...)
        dto.setTelefono(tel);
    }

    private void validarCamposActualizar(EmpleadosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() || !dto.getEmail().contains("@")) {
            throw new RuntimeException("Email obligatorio");
        }

        dto.setTelefono(normalizarYValidarTelefono(dto.getTelefono()));
        // password / rol / fechaIngreso / estado: opcionales en update
    }

    /**
     * Normaliza el estado para soportar un <select> "Sí/No"
     * - null o vacío => "activo"
     * - "si"/"sí"/"true"/"1"/"activo" => "activo"
     * - "no"/"false"/"0"/"inactivo" => "inactivo"
     */
    private String normalizarEstado(String estado) {
        if (estado == null || estado.isBlank()) return "activo";

        String s = estado.trim().toLowerCase();

        return switch (s) {
            case "activo", "si", "sí", "true", "1" -> "activo";
            case "inactivo", "no", "false", "0" -> "inactivo";
            default -> throw new RuntimeException("Estado inválido (use Sí/No o activo/inactivo)");
        };
    }

    /**
     * Igual que en tu ClienteService:
     * - null/blank => null
     * - quita espacios internos
     * - solo dígitos
     * - exige 9 dígitos
     */
    private String normalizarYValidarTelefono(String telefono) {
        if (telefono == null) return null;

        String t = telefono.trim();
        if (t.isBlank()) return null;

        t = t.replaceAll("\\s+", "");

        if (!t.matches("\\d+")) {
            throw new RuntimeException("El teléfono solo puede contener números (0-9)");
        }

        if (t.length() != 9) {
            throw new IllegalArgumentException("Pero que numero es ese payaso, ponme uno de verdad");
        }

        return t;
    }
}
