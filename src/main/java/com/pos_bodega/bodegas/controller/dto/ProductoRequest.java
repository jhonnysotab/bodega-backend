package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Setter
@Getter
public class ProductoRequest {
  private String nombre;
  private String codigoBarras;
  private BigDecimal precioCompra;
  private BigDecimal precioVenta;
  private Integer stock;
  private Integer stockMinimo;
  private Long categoriaId;
}