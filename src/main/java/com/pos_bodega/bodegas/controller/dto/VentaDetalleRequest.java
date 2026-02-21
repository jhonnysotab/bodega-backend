package com.pos_bodega.bodegas.controller.dto;


import lombok.Data;

@Data
public class VentaDetalleRequest {
  private Long productoId;
  private Integer cantidad;
}