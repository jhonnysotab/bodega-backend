package com.pos_bodega.bodegas.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private String username;
  private String nombre;
  private String rol;
  private Long empresaId;
  private String nombreEmpresa;
}