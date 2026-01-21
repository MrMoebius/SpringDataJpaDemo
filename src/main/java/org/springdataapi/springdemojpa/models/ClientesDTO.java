package org.springdataapi.springdemojpa.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
public class
ClientesDTO {

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


}