package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmpleadosRepository extends ListCrudRepository<Empleados, Integer> {

    Optional<Empleados> findFirstByNombreIgnoreCase(String nombre);

    Optional<Empleados> findByEmail(String email);

    Optional<Empleados> findByTelefono(String telefono);

    Optional<Empleados> findFirstByFechaIngreso(LocalDate fechaIngreso);

    Optional<Empleados> findFirstByEstadoIgnoreCase(String estado);
}

