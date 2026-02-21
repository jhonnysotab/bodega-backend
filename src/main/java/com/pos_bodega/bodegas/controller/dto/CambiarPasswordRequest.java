package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CambiarPasswordRequest {
  private String passwordActual;
  private String passwordNueva;
  private String passwordNuevaConfirmacion;
}