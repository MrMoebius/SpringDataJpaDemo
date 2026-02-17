package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientesRepository extends JpaRepository<Clientes, Integer>
{

    public Optional<Clientes> findByEmail(String email);
    public Optional<Clientes> findByTelefono(String telefono);

    public boolean existsByTelefono(String telefono);
    public boolean existsByEmail(String email);


    @Query("""
        SELECT c
        FROM Clientes c
        WHERE c.idEmpleadoResponsable.id = :idEmpleado
          AND c.fechaAlta >= :fechaDesde
        ORDER BY c.fechaAlta DESC
    """)
    public List<Clientes> BusacarClientePorEmpleadoyFecha(@Param("idEmpleado") Integer idEmpleado , @Param("fechaDesde")LocalDate fechaDesde);

    @Query("""
        SELECT c
        FROM Clientes c
        WHERE (:telefono IS NULL OR :telefono = '' OR c.telefono LIKE CONCAT('%', :telefono, '%'))
          AND (:email IS NULL OR :email = '' OR c.email LIKE CONCAT('%', :email, '%'))
          AND (:tipoCliente IS NULL OR :tipoCliente = '' OR c.tipoCliente = :tipoCliente)
          AND (:idEmpleado IS NULL OR c.idEmpleadoResponsable.id = :idEmpleado)
          AND (:fechaDesde IS NULL OR c.fechaAlta >= :fechaDesde)
        ORDER BY c.id
    """)
    public List<Clientes> buscarClientesFiltrados(
            @Param("telefono") String telefono,
            @Param("email") String email,
            @Param("tipoCliente") String tipoCliente,
            @Param("idEmpleado") Integer idEmpleado,
            @Param("fechaDesde") LocalDate fechaDesde);
}
