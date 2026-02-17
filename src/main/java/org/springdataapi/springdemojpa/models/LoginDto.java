package org.springdataapi.springdemojpa.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
