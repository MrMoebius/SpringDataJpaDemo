package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.RolesEmpleado;
import org.springdataapi.springdemojpa.repository.RolesEmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolesEmpleadoService {

    private final RolesEmpleadoRepository repo;

    public RolesEmpleadoService(RolesEmpleadoRepository repo) {
        this.repo = repo;
    }

    public List<RolesEmpleado> findAll() {
        return repo.findAll();
    }
}

