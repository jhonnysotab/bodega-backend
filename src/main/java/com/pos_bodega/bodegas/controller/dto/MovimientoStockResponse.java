package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MovimientoStockResponse {
  private Long id;
  private Long productoId;
  private String productoNombre;
  private Long proveedorId;
  private String proveedorNombre;
  private String tipoMovimiento;
  private Integer cantidad;
  private Integer stockAnterior;
  private Integer stockNuevo;
  private String nota;
  private Long usuarioId;
  private String usuarioNombre;
  private LocalDateTime fecha;
}