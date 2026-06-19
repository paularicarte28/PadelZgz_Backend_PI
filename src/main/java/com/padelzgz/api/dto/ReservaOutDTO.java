package com.padelzgz.api.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ReservaOutDTO {
    private long id;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private float precio;
    private boolean pagado;
    private String comentario;
    // Datos de la pista
    private Long pistaId;
    private int pistaNumero;
    private String pistaTipo;
    private String pistaSuperficie;
    // Datos del usuario
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioApellidos;
    private String usuarioEmail;
}