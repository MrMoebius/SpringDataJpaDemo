package org.springdataapi.springdemojpa.repository;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springframework.data.repository.ListCrudRepository;

public interface EmpleadosRepository  extends ListCrudRepository<Empleados, Integer>
{

}
