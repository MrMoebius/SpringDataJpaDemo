package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class EmpleadosService {

    private final EmpleadosRepository empleadosRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository) {
        this.empleadosRepository = empleadosRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findById(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un entero positivo");
        }
        try {
            return empleadosRepository.findById(id);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findById)", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        try {
            return empleadosRepository.findFirstByNombreIgnoreCase(nombre.trim());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findByNombre)", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        try {
            return empleadosRepository.findByEmail(email.trim());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findByEmail)", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findByTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        try {
            return empleadosRepository.findByTelefono(telefono.trim());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findByTelefono)", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findByFechaIngreso(LocalDate fechaIngreso) {
        if (fechaIngreso == null) {
            throw new IllegalArgumentException("La fecha de ingreso es obligatoria");
        }
        try {
            return empleadosRepository.findFirstByFechaIngreso(fechaIngreso);
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findByFechaIngreso)", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Empleados> findByEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new IllegalArgumentException("El estado es obligatorio");
        }
        try {
            return empleadosRepository.findFirstByEstadoIgnoreCase(estado.trim());
        } catch (DataAccessException e) {
            throw new RuntimeException("Error accediendo a la base de datos (findByEstado)", e);
        }
    }
}
