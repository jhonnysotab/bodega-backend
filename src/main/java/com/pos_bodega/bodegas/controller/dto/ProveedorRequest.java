package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProveedorRequest {
  private String nombre;
  private String empresaProveedor;
  private String telefono;
  private List<Long> categoriaIds;  // IDs de las categorías que maneja
}