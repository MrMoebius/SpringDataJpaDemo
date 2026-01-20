package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadosService
{
    EmpleadosRepository empleadosRepository;

    public EmpleadosService(EmpleadosRepository empleadosRepository)
    {
        this.empleadosRepository = empleadosRepository;

    }

    public List<Empleados> findAll()
    {
        return empleadosRepository.findAll();
    }
}
