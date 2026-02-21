package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Data
@Getter
@Setter
public class EmpresaRequest {
  private String nombre;
  private String ruc;
  private String telefono;
  private String direccion;
  private String plan;          // Ej: "BASICO", "PRO", "ENTERPRISE"
  private LocalDate fechaInicio;
  private LocalDate fechaVencimiento;
}