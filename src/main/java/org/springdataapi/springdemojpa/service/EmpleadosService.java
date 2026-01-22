package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.EmpleadosDTO;
import org.springdataapi.springdemojpa.models.RolesEmpleado;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.repository.RolesEmpleadoRepository;
import org.springframework.data.repository.query.Param;
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

    public Optional<Empleados> findByEmail(String email) {
        if (email == null || email.isBlank()) throw new RuntimeException("Email obligatorio");
        return Optional.ofNullable(empleadosRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado por email")));
    }

    public Optional<Empleados> findByTelefono(String telefono) {
        String tel = normalizarYValidarTelefono(telefono); // ✅ solo números
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

        if (empleadosRepository.existsByEmail(dto.getEmail().trim())) {
            throw new RuntimeException("Email ya registrado");
        }

        String telefonoNormalizado = normalizarYValidarTelefono(dto.getTelefono());

        if (telefonoNormalizado != null && empleadosRepository.existsByTelefono(telefonoNormalizado)) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        RolesEmpleado rol = rolesEmpleadoRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Empleados e = new Empleados();
        e.setNombre(dto.getNombre().trim());
        e.setEmail(dto.getEmail().trim());
        e.setTelefono(telefonoNormalizado);
        e.setPassword(dto.getPassword()); // aquí no has pedido hash
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
        empleadosRepository.deleteById(id);
    }

    public Empleados actualizar(Integer id, EmpleadosDTO dto) {
        if (id == null) throw new RuntimeException("Id obligatorio");
        validarCamposActualizar(dto);

        Empleados e = empleadosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        String nuevoEmail = dto.getEmail().trim();
        if (!e.getEmail().equals(nuevoEmail) && empleadosRepository.existsByEmail(nuevoEmail)) {
            throw new RuntimeException("Email ya registrado");
        }

        String nuevoTelefono = normalizarYValidarTelefono(dto.getTelefono());

        if (nuevoTelefono != null && (e.getTelefono() == null || !nuevoTelefono.equals(e.getTelefono()))
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

    public List<Empleados> BuscarPorLetras(String letra)
    {
        return empleadosRepository.buscarPorLetra(letra);
    }

    public List<Empleados> buscarEmpleadosFiltrados(
            String telefono, String email, String estado, 
            Integer idRol, Boolean tieneClientes, LocalDate fechaIngreso) {
        return empleadosRepository.buscarEmpleadosFiltrados(
                telefono, email, estado, idRol, tieneClientes, fechaIngreso);
    }

    private void validarCamposCrear(EmpleadosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("Nombre obligatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email obligatorio");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password obligatorio");
        }
        if (dto.getIdRol() == null) {
            throw new RuntimeException("Rol obligatorio");
        }
    }

    private void validarCamposActualizar(EmpleadosDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("Nombre obligatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email obligatorio");
        }
        // password / rol / fechaIngreso / estado: opcionales en update
    }

    /**
     * ✅ Normaliza el estado para soportar un <select> "Sí/No"
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

    private String normalizarYValidarTelefono(String telefono) {
        if (telefono == null) return null;

        String t = telefono.trim();
        if (t.isBlank() && t.length() < 10) return null;

        t = t.replaceAll("\\s+", ""); // esto quita espacios

        if (t.length() > 9) {
            throw new IllegalArgumentException("Pero que número es ese subnormal");
        }

        if (!t.matches("\\d+")) {
            throw new RuntimeException("El teléfono solo puede contener números (0-9)");
        }
        return t;
    }
}
