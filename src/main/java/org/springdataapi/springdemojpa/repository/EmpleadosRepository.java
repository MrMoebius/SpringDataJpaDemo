package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmpleadosRepository extends JpaRepository<Empleados, Integer> {


    Optional<Empleados> findByEmail(String email);
    Optional<Empleados> findByTelefono(String telefono);

    boolean existsByEmail(String email);
    boolean existsByTelefono(String telefono);

    List<Empleados> findByEstado(String estado);
    List<Empleados> findByFechaIngreso(LocalDate fechaIngreso);

    @Query("SELECT e FROM Empleados e " +
            "WHERE LOWER(e.nombre) LIKE CONCAT('%', LOWER(:letra), '%') ")
    List<Empleados> buscarPorLetra(@Param("letra") String letra);
}
