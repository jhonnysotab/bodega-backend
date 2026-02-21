package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class ProductoResponse {
  private Long id;
  private String nombre;
  private String codigoBarras;
  private BigDecimal precioCompra;
  private BigDecimal precioVenta;
  private BigDecimal margenGanancia;   // % ganancia calculado automáticamente
  private Integer stock;
  private Integer stockMinimo;
  private Boolean stockBajo;           // true si stock <= stockMinimo
  private Long categoriaId;
  private String categoriaNombre;      // nombre de la categoría
  private Boolean activo;
  private Long empresaId;
  private LocalDateTime fechaCreacion;
}