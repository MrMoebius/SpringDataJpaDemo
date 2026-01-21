package org.springdataapi.springdemojpa.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductosDTO {

    private Integer id;

    @NotBlank(message = "Nombre obligatorio")
    @Size(max = 150, message = "Máx. 150 caracteres")
    private String nombre;

    @Size(max = 255, message = "Máx. 255 caracteres")
    private String descripcion;

    @Size(max = 255, message = "Máx. 255 caracteres")
    private String categoria;

    private Double precio;

    private Boolean activo;
}