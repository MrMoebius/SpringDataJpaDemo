package org.springdataapi.springdemojpa.service;

import org.springdataapi.springdemojpa.models.Clientes;
import org.springdataapi.springdemojpa.models.ClientesDTO;
import org.springdataapi.springdemojpa.models.Empleados;
import org.springdataapi.springdemojpa.repository.ClientesRepository;
import org.springdataapi.springdemojpa.repository.EmpleadosRepository;
import org.springframework.cglib.core.Local;
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
        if (id == null) throw new RuntimeException("Id obligatorio");
        return clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Optional<Clientes> findByEmail(String email) {
        if (email == null || email.isBlank()) throw new RuntimeException("Email obligatorio");
        String e = email.trim();
        return Optional.ofNullable(clientesRepository.findByEmail(e)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado por email")));
    }

    public Clientes findByEmailOrThrow(String email) {
        if (email == null || email.isBlank()) throw new RuntimeException("Email obligatorio");
        return clientesRepository.findByEmail(email.trim())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
    }

    public Optional<Clientes> findByTelefono(String telefono) {
        String tel = normalizarYValidarTelefono(telefono);
        if (tel == null) throw new RuntimeException("Teléfono obligatorio");

        return Optional.ofNullable(clientesRepository.findByTelefono(tel)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado por teléfono")));
    }

    public void crear(ClientesDTO dto) {
        validarCampos(dto);

        String email = dto.getEmail().trim();
        String telefono = normalizarYValidarTelefono(dto.getTelefono());

        if (clientesRepository.existsByEmail(email)) {
            throw new RuntimeException("Email ya registrado");
        }
        if (telefono != null && clientesRepository.existsByTelefono(telefono)) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        Clientes cliente = new Clientes();
        cliente.setNombre(dto.getNombre().trim());
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
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
        if (id == null) throw new RuntimeException("Id obligatorio");
        if (!clientesRepository.existsById(id)) {
            throw new RuntimeException("Cliente no existe");
        }
        clientesRepository.deleteById(id);
    }

    public Clientes actualizar(Integer id, ClientesDTO dto) {
        if (id == null) throw new RuntimeException("Id obligatorio");

        Clientes cliente = clientesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        validarCamposActualizar(dto);

        String nuevoEmail = dto.getEmail().trim();
        String nuevoTelefono = normalizarYValidarTelefono(dto.getTelefono());

        if (!cliente.getEmail().equals(nuevoEmail) && clientesRepository.existsByEmail(nuevoEmail)) {
            throw new RuntimeException("Email ya registrado");
        }

        if (nuevoTelefono != null
                && (cliente.getTelefono() == null || !nuevoTelefono.equals(cliente.getTelefono()))
                && clientesRepository.existsByTelefono(nuevoTelefono)) {
            throw new RuntimeException("Teléfono ya registrado");
        }

        cliente.setNombre(dto.getNombre().trim());
        cliente.setEmail(nuevoEmail);
        cliente.setTelefono(nuevoTelefono);
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

    public List<Clientes> BuscarClientePorEmpleadoyFecha(Integer idEmpleado, LocalDate  fechaDesde)
    {
        return  clientesRepository.BusacarClientePorEmpleadoyFecha(idEmpleado, fechaDesde);
    }

    public List<Clientes> buscarClientesFiltrados(
            String telefono, String email, String tipoCliente, 
            Integer idEmpleado, LocalDate fechaDesde) {
        return clientesRepository.buscarClientesFiltrados(
                telefono, email, tipoCliente, idEmpleado, fechaDesde);
    }

    private void validarCampos(ClientesDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            dto.setPassword("PENDIENTE");
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() || !dto.getEmail().contains("@")) {
            throw new RuntimeException("Email obligatorio");
        }

        // Normaliza y valida teléfono
        dto.setTelefono(normalizarYValidarTelefono(dto.getTelefono()));

        if (dto.getTipo_cliente() != null && !dto.getTipo_cliente().trim().isEmpty()) {
            String tipo = dto.getTipo_cliente().trim().toUpperCase();
            if (tipo.equals("PERSONA") || tipo.equals("PARTICULAR")) {
                dto.setTipo_cliente("PARTICULAR");
            } else if (tipo.equals("EMPRESA")) {
                dto.setTipo_cliente("EMPRESA");
            } else if (!tipo.equals("PARTICULAR") && !tipo.equals("EMPRESA")) {
                throw new IllegalArgumentException(
                        "Tipo de cliente inválido: " + dto.getTipo_cliente() +
                                ". Valores válidos: PARTICULAR, EMPRESA"
                );
            } else {
                dto.setTipo_cliente(tipo);
            }
        }
    }

    private void validarCamposActualizar(ClientesDTO dto) {
        if (dto == null) throw new RuntimeException("DTO obligatorio");

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("Nombre obligatorio");
        }

        // Password:si viene vacío, se mantiene la actual

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty() || !dto.getEmail().contains("@")) {
            throw new RuntimeException("Email obligatorio");
        }

        // Normaliza y valida teléfono.
        dto.setTelefono(normalizarYValidarTelefono(dto.getTelefono()));

        if (dto.getTipo_cliente() != null && !dto.getTipo_cliente().trim().isEmpty()) {
            String tipo = dto.getTipo_cliente().trim().toUpperCase();
            if (tipo.equals("PERSONA") || tipo.equals("PARTICULAR")) {
                dto.setTipo_cliente("PARTICULAR");
            } else if (tipo.equals("EMPRESA")) {
                dto.setTipo_cliente("EMPRESA");
            } else if (!tipo.equals("PARTICULAR") && !tipo.equals("EMPRESA")) {
                throw new IllegalArgumentException(
                        "Tipo de cliente inválido: " + dto.getTipo_cliente() +
                                ". Valores válidos: PARTICULAR, EMPRESA"
                );
            } else {
                dto.setTipo_cliente(tipo);
            }
        }
    }

    /**
     * Devuelve null si viene vacío. Si viene informado, exige SOLO dígitos.
     * Además elimina espacios internos ("600 123 123" -> "600123123").
     */
    private String normalizarYValidarTelefono(String telefono) {
        if (telefono == null) return null;

        String t = telefono.trim();
        if (t.isBlank()) return null;

        t = t.replaceAll("\\s+", ""); // esto quita espacios

        if (t.length() > 9) {
            throw new IllegalArgumentException("El teléfono supera la longitud máxima permitida.");
        }

        if (!t.matches("\\d+")) {
            throw new RuntimeException("El teléfono solo puede contener números (0-9)");
        }

        return t;
    }
}
