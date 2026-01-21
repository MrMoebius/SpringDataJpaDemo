package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadosRepository extends ListCrudRepository<Empleados, Integer> {

    Empleados findByNombre(String nombre);

    Optional<Empleados> findByEmail(String email);
    Optional<Empleados> findByTelefono(String telefono);

    boolean existsByEmail(String email);
    boolean existsByTelefono(String telefono);

    List<Empleados> findByEstado(String estado);
    List<Empleados> findByFechaIngreso(LocalDate fechaIngreso);
}
