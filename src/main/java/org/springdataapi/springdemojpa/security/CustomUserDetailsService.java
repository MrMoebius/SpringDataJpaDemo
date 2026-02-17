package org.springdataapi.springdemojpa.security;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.repository.ClientesRepository;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpleadosRepository empleadosRepository;
    private final ClientesRepository clientesRepository;

    public CustomUserDetailsService(EmpleadosRepository empleadosRepository,
                                    ClientesRepository clientesRepository) {
        this.empleadosRepository = empleadosRepository;
        this.clientesRepository = clientesRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Primero busca en empleados
        Optional<Empleados> empleadoOpt = empleadosRepository.findByEmail(username);
        if (empleadoOpt.isPresent()) {
            Empleados empleado = empleadoOpt.get();
            // Acceder al rol dentro de la transacción para evitar LazyInitializationException
            String role = empleado.getIdRol() != null ? 
                empleado.getIdRol().getNombreRol().toUpperCase() : "EMPLEADO";
            
            // Si el rol es "ADMIN", se asigna ese rol
            if (role.equals("ADMIN")) {
                return new CustomUserDetails(
                    empleado.getEmail(),
                    empleado.getPassword(),
                    "ADMIN",
                    empleado.getId(),
                    "ADMIN"
                );
            } else {
                return new CustomUserDetails(
                    empleado.getEmail(),
                    empleado.getPassword(),
                    "EMPLEADO",
                    empleado.getId(),
                    "EMPLEADO"
                );
            }
        }

        // Si no está en empleados, busca en clientes
        Optional<Clientes> clienteOpt = clientesRepository.findByEmail(username);
        if (clienteOpt.isPresent()) {
            Clientes cliente = clienteOpt.get();
            return new CustomUserDetails(
                cliente.getEmail(),
                cliente.getPassword(),
                "CLIENTE",
                cliente.getId(),
                "CLIENTE"
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado: " + username);
    }
}
