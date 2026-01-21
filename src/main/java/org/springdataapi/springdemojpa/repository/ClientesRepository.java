package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientesRepository extends ListCrudRepository<Clientes, Integer>
{

    public Clientes findByNombre(String nombre);
    public Optional<Clientes> findByEmail(String email);
    public Optional<Clientes> findByTelefono(String telefono);

    public boolean existsByTelefono(String telefono);
    public boolean existsByEmail(String email);


}
