package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.ClientesDTO;
import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.repository.ClientesRepository;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClientesRepository clientesRepository;
    private final EmpleadosRepository empleadosRepository;

    public ClienteService(ClientesRepository clientesRepository,
                          EmpleadosRepository empleadosRepository) {
        this.clientesRepository = clientesRepository;
        this.empleadosRepository = empleadosRepository;
    }

    public List<Clientes> findAll() {
        return clientesRepository.findAll();
    }

    public Clientes findById(Integer id) {
        return clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Optional<Clientes> findByEmail(String email) {
        return Optional.ofNullable(clientesRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado por email")));
    }

    public Optional<Clientes> findByTelefono(String telefono) {
        return Optional.ofNullable(clientesRepository.findByTelefono(telefono)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado por teléfono")));
    }

    public void crear(ClientesDTO dto) {

        validarCampos(dto);

        if (clientesRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }
        if (dto.getTelefono() != null && clientesRepository.existsByTelefono(dto.getTelefono())) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        Clientes cliente = new Clientes();
        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setPassword(dto.getPassword());
        cliente.setTipoCliente(dto.getTipo_cliente());
        cliente.setFechaAlta(LocalDate.now());

        if (dto.getId_empleadoresponsable() != null) {
            Empleados empleado = empleadosRepository.findById(dto.getId_empleadoresponsable())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            cliente.setIdEmpleadoResponsable(empleado);
        }

        clientesRepository.save(cliente);
    }

    public void eliminar(Integer id) {
        if (!clientesRepository.existsById(id))
        {
            throw new RuntimeException("Cliente no existe");
        }
        clientesRepository.deleteById(id);
    }

    public Clientes actualizar(Integer id, ClientesDTO dto) {
        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        if (!cliente.getEmail().equals(dto.getEmail()) && clientesRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email ya registrado");
        }
        if (dto.getTelefono() != null && !dto.getTelefono().equals(cliente.getTelefono())
                && clientesRepository.existsByTelefono(dto.getTelefono())) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipoCliente(dto.getTipo_cliente());


        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            cliente.setPassword(dto.getPassword());
        }


        if (dto.getId_empleadoresponsable() != null) {
            Empleados empleado = empleadosRepository.findById(dto.getId_empleadoresponsable())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
            cliente.setIdEmpleadoResponsable(empleado);
        } else {
            cliente.setIdEmpleadoResponsable(null);
        }

        return clientesRepository.save(cliente);
    }



    private void validarCampos(ClientesDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            dto.setPassword("PonleContraseñaSubnormal");
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty())
        {
            if (!dto.getEmail().contains("@"))
            {
                throw new RuntimeException("Email obligatorio");
            }
        }
        if (dto.getTelefono() != null && dto.getTelefono().length() > 20) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }

        if (dto.getTipo_cliente() != null && !dto.getTipo_cliente().trim().isEmpty()) {
            String tipo = dto.getTipo_cliente().trim().toUpperCase();
            if (tipo.equals("PERSONA") || tipo.equals("PARTICULAR")) {
                dto.setTipo_cliente("PARTICULAR");
            } else if (tipo.equals("EMPRESA")) {
                dto.setTipo_cliente("EMPRESA");
            } else if (!tipo.equals("PARTICULAR") && !tipo.equals("EMPRESA")) {
                throw new IllegalArgumentException("Tipo de cliente inválido: " + dto.getTipo_cliente() +
                        ". Valores válidos: PARTICULAR, EMPRESA");
            } else {
                dto.setTipo_cliente(tipo);
            }
        }
    }
}
