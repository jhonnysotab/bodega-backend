package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * Línea de producto extraída de una foto de voucher/factura por el modelo de visión.
 * precioVenta y categoría no vienen de la factura: el usuario los completa en la vista previa.
 */
@Data
public class ProductoImportadoDTO {
  private String nombre;
  private Integer cantidad;
  private BigDecimal precioCompra;
}
