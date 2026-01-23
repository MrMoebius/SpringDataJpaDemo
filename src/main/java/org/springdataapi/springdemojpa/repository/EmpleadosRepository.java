package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("""
        SELECT DISTINCT e
        FROM Empleados e
        WHERE (:telefono IS NULL OR :telefono = '' OR e.telefono LIKE CONCAT('%', :telefono, '%'))
          AND (:email IS NULL OR :email = '' OR e.email LIKE CONCAT('%', :email, '%'))
          AND (:estado IS NULL OR :estado = '' OR e.estado = :estado)
          AND (:idRol IS NULL OR e.idRol.id = :idRol)
          AND (:fechaIngreso IS NULL OR e.fechaIngreso >= :fechaIngreso)
          AND (:tieneClientes IS NULL OR 
               (:tieneClientes = true AND (SELECT COUNT(c) FROM Clientes c WHERE c.idEmpleadoResponsable.id = e.id) > 0) OR
               (:tieneClientes = false AND (SELECT COUNT(c) FROM Clientes c WHERE c.idEmpleadoResponsable.id = e.id) = 0))
        ORDER BY e.id
    """)
    List<Empleados> buscarEmpleadosFiltrados(
            @Param("telefono") String telefono,
            @Param("email") String email,
            @Param("estado") String estado,
            @Param("idRol") Integer idRol,
            @Param("tieneClientes") Boolean tieneClientes,
            @Param("fechaIngreso") LocalDate fechaIngreso);
}
