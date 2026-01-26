package org.springdataapi.springdemojpa.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class
EmpleadosDTO {

    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    private String telefono;

    private String password;

    private Integer idRol;

    private LocalDate fechaIngreso;

    private String estado; // si viene null es "activo"

}
