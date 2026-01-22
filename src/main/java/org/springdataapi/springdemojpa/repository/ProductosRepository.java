package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Integer>
{

    boolean existsByNombre(String nombre);

    List<Productos> findByCategoria(String categoria);

    List<Productos> findByActivo(Boolean activo);

    List<Productos> findByPrecio(Double precio);

    @Query("SELECT p FROM Productos p " +
            "WHERE p.activo = true " +
            "AND (:categoria IS NULL OR p.categoria = :categoria) " +
            "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
            "ORDER BY p.precio ASC")
    List<Productos> buscarProductosFiltrados(@Param("categoria") String categoria,
                                    @Param("precioMin") Double precioMin);

    @Query("""
        SELECT p
        FROM Productos p
        WHERE (:nombre IS NULL OR :nombre = '' OR p.nombre LIKE CONCAT('%', :nombre, '%'))
          AND (:categoria IS NULL OR :categoria = '' OR p.categoria = :categoria)
          AND (:precioMin IS NULL OR p.precio >= :precioMin)
          AND (:precioMax IS NULL OR p.precio <= :precioMax)
          AND (:activo IS NULL OR p.activo = :activo)
        ORDER BY p.id
    """)
    List<Productos> buscarProductosFiltradosCompleto(
            @Param("nombre") String nombre,
            @Param("categoria") String categoria,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax,
            @Param("activo") Boolean activo);

}
