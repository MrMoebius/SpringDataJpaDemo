package org.springdataapi.springdemojpa.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "facturas")
public class Facturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura", nullable = false)
    private Integer id;

    @Size(max = 50)
    @NotNull
    @Column(name = "num_factura", nullable = false, length = 50)
    private String numFactura;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente_pagador", nullable = false)
    private Clientes idClientePagador;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "id_empleado")
    private Empleados idEmpleado;

    @NotNull
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull
    @Column(name = "total", nullable = false)
    private Double total;

    @Size(max = 50)
    @ColumnDefault("'PENDIENTE'")
    @Column(name = "estado", length = 50)
    private String estado;

    @Size(max = 150)
    @NotNull
    @Column(name = "notas", nullable = false, length = 150)
    private String notas;

}