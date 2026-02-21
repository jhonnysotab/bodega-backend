package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class EmpresaResponse {
  private Long id;
  private String nombre;
  private String ruc;
  private String telefono;
  private String direccion;
  private String plan;
  private LocalDate fechaInicio;
  private LocalDate fechaVencimiento;
  private Boolean activo;
  private LocalDateTime fechaCreacion;
  private String estadoPlan;    // "VIGENTE" o "VENCIDO"
}