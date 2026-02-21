package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GastoRequest {
  private String descripcion;
  private BigDecimal monto;
  private LocalDate fecha;
}