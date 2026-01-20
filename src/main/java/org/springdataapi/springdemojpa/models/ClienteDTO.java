package org.springdataapi.springdemojpa.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public class ClienteDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    private String password;

    private String tipo_cliente;

    private LocalDate fecha_alta;

    private Integer id_empleadoresponsable;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipo_cliente() { return tipo_cliente; }
    public void setTipo_cliente(String tipo_cliente) { this.tipo_cliente = tipo_cliente; }

    public LocalDate getFecha_alta() { return fecha_alta; }
    public void setFecha_alta(LocalDate fecha_alta) { this.fecha_alta = fecha_alta; }

    public Integer getId_empleadoresponsable() { return id_empleadoresponsable; }
    public void setId_empleadoresponsable(Integer id_empleadoresponsable) {
        this.id_empleadoresponsable = id_empleadoresponsable;
    }
}