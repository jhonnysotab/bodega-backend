package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProveedorResponse {
  private Long id;
  private String nombre;
  private String empresaProveedor;
  private String telefono;
  private Boolean activo;
  private Long empresaId;
  private LocalDateTime fechaCreacion;
  private List<CategoriaInfo> categorias;  // categorías que maneja el proveedor

  @Data
  public static class CategoriaInfo {
    private Long id;
    private String nombre;
  }
}