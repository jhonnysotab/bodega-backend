package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;

@Data
public class MovimientoStockRequest {
  private Long productoId;
  private Long proveedorId;       // obligatorio si es INGRESO
  private String tipoMovimiento;  // INGRESO o AJUSTE
  private Integer cantidad;
  private String nota;
}