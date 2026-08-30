package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CajaAperturaRequest {
  private BigDecimal montoInicial;
  private String nota;
}
