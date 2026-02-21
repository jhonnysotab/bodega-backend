package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UsuarioRequest {
  private String nombre;
  private String username;
  private String password;
  private Long empresas;
  private String rol; // "ADMIN" o "CAJERO"
}