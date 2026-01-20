package org.springdataapi.springdemojpa.service;


import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.models.Facturas;
import org.springdataapi.springdemojpa.repository.ClientesRepository;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springdataapi.springdemojpa.repository.FacturasRepository;
import org.springframework.stereotype.Service;

@Service
public class FacturaService {

    FacturasRepository facturasRepository;

    public FacturaService(FacturasRepository facturasRepository, EmpleadosRepository empleadosRepository) {

        this.facturasRepository = facturasRepository;

    }

    public Facturas getFacturas() {}

}
