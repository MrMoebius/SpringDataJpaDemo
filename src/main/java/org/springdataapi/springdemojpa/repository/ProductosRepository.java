package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Productos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductosRepository extends JpaRepository<Productos, Integer> {

    Optional<Productos> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Productos> findByCategoria(String categoria);

    List<Productos> findByActivo(Boolean activo);

    List<Productos> findByPrecio(Double precio);
}
