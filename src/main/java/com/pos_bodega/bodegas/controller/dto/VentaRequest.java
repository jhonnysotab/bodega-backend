package com.pos_bodega.bodegas.controller.dto;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class VentaRequest {
  private String tipoPago;         // EFECTIVO, YAPE, PLIN, TRANSFERENCIA
  private BigDecimal montoRecibido;
  private String nota;
  private List<VentaDetalleRequest> detalles;
}
