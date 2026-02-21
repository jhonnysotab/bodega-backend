package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class VentaDetalleResponse {
  private Long productoId;
  private String productoNombre;
  private Integer cantidad;
  private BigDecimal precioUnitario;
  private BigDecimal subtotal;
}