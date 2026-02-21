package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GastoResponse {
  private Long id;
  private String descripcion;
  private BigDecimal monto;
  private LocalDate fecha;
  private Long usuarioId;
  private String usuarioNombre;
}