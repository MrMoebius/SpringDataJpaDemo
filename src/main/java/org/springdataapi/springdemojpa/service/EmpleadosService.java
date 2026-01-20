package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.EmpleadoDTO;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.RolesEmpleado;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.repository.RolesEmpleadoRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpleadosService {

    private final EmpleadosRepository empleadosRepository;
    private final RolesEmpleadoRepository rolesEmpleadoRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository,
                            RolesEmpleadoRepository rolesEmpleadoRepository) {
        this.empleadosRepository = empleadosRepository;
        this.rolesEmpleadoRepository = rolesEmpleadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Empleados> findAll() {
        try {
            return empleadosRepository.findAll();
        } catch (DataAccessException e) {
            throw new RuntimeException("Error BD (findAll empleados)", e);
        }
    }

    @Transactional(readOnly = true)
    public Empleados findById(Integer id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id inválido");
        try {
            return empleadosRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        } catch (DataAccessException e) {
            throw new RuntimeException("Error BD (findById empleados)", e);
        }
    }

    @Transactional
    public void crear(EmpleadoDTO dto) {
        validarCrear(dto);

        try {
            if (empleadosRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email ya registrado");
            }
            if (dto.getTelefono() != null && empleadosRepository.existsByTelefono(dto.getTelefono())) {
                throw new RuntimeException("Teléfono ya registrado");
            }

            RolesEmpleado rol = rolesEmpleadoRepository.findById(dto.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Empleados e = new Empleados();
            e.setNombre(dto.getNombre().trim());
            e.setEmail(dto.getEmail().trim());
            e.setTelefono(dto.getTelefono() == null ? null : dto.getTelefono().trim());
            e.setPassword(dto.getPassword());
            e.setRol(rol);

            e.setFechaIngreso(dto.getFechaIngreso() != null ? dto.getFechaIngreso() : LocalDate.now());
            e.setEstado((dto.getEstado() == null || dto.getEstado().isBlank()) ? "activo" : dto.getEstado().trim());

            empleadosRepository.save(e);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error BD (crear empleado)", ex);
        }
    }

    @Transactional
    public Empleados actualizar(Integer id, EmpleadoDTO dto) {
        validarActualizar(dto);

        try {
            Empleados e = empleadosRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            if (!e.getEmail().equals(dto.getEmail()) && empleadosRepository.existsByEmail(dto.getEmail())) {
                throw new RuntimeException("Email ya registrado");
            }
            if (dto.getTelefono() != null && !dto.getTelefono().equals(e.getTelefono())
                    && empleadosRepository.existsByTelefono(dto.getTelefono())) {
                throw new RuntimeException("Teléfono ya registrado");
            }

            e.setNombre(dto.getNombre().trim());
            e.setEmail(dto.getEmail().trim());
            e.setTelefono(dto.getTelefono() == null ? null : dto.getTelefono().trim());

            if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
                e.setEstado(dto.getEstado().trim());
            }
            if (dto.getFechaIngreso() != null) {
                e.setFechaIngreso(dto.getFechaIngreso());
            }

            // Solo si viene password
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                e.setPassword(dto.getPassword());
            }

            if (dto.getIdRol() != null) {
                RolesEmpleado rol = rolesEmpleadoRepository.findById(dto.getIdRol())
                        .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                e.setRol(rol);
            }

            return empleadosRepository.save(e);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error BD (actualizar empleado)", ex);
        }
    }

    @Transactional
    public void eliminar(Integer id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Id inválido");
        try {
            if (!empleadosRepository.existsById(id)) {
                throw new RuntimeException("Empleado no existe");
            }
            empleadosRepository.deleteById(id);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Error BD (eliminar empleado)", ex);
        }
    }

    private void validarCrear(EmpleadoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) throw new RuntimeException("Nombre obligatorio");
        if (dto.getEmail() == null || dto.getEmail().isBlank()) throw new RuntimeException("Email obligatorio");
        if (dto.getPassword() == null || dto.getPassword().isBlank()) throw new RuntimeException("Password obligatorio");
        if (dto.getIdRol() == null) throw new RuntimeException("Rol obligatorio");
    }

    private void validarActualizar(EmpleadoDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) throw new RuntimeException("Nombre obligatorio");
        if (dto.getEmail() == null || dto.getEmail().isBlank()) throw new RuntimeException("Email obligatorio");
        // password no obligatorio en update
    }
}
