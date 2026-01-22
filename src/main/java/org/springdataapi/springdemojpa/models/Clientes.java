package org.springdataapi.springdemojpa.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "clientes")
public class Clientes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 150)
    @Column(name = "email", length = 150)
    private String email;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 50)
    @Column(name = "tipo_cliente", length = 50)
    private String tipoCliente;

    @Size(max = 150)
    @NotNull
    @Column(name = "password", nullable = false, length = 150)
    private String password;

    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_empleado_responsable")
    private Empleados idEmpleadoResponsable;
    }