package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UsuarioResponse {
  private Long id;
  private String nombre;
  private String username;
  private String rol;
  private Boolean activo;
  private Long empresaId;
}