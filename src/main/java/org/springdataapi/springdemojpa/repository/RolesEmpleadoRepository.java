package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.RolesEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesEmpleadoRepository extends JpaRepository<RolesEmpleado, Integer> {

    Optional<RolesEmpleado> findByNombreRol(String nombreRol);

    boolean existsByNombreRol(String nombreRol);
}

