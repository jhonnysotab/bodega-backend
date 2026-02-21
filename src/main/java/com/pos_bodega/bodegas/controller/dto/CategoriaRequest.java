package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CategoriaRequest {
  private String nombre;
  private String descripcion;
  private Long empresaId;
}