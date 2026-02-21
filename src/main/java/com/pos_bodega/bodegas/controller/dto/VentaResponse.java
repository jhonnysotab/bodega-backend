package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VentaResponse {
  private Long id;
  private String numeroVenta;
  private BigDecimal total;
  private String tipoPago;
  private BigDecimal montoRecibido;
  private BigDecimal vuelto;
  private String nota;
  private Long usuarioId;
  private String usuarioNombre;
  private LocalDateTime fechaVenta;
  private List<VentaDetalleResponse> detalles;
}