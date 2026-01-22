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

}
