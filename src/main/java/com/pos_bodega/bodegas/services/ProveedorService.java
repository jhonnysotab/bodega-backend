package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.ProveedorRequest;
import com.pos_bodega.bodegas.controller.dto.ProveedorResponse;

import java.util.List;

public interface ProveedorService {

  ProveedorResponse crear(ProveedorRequest request, Long empresaId);

  ProveedorResponse actualizar(Long id, ProveedorRequest request, Long empresaId);

  List<ProveedorResponse> listar(Long empresaId);

  List<ProveedorResponse> listarActivos(Long empresaId);

  ProveedorResponse obtener(Long id, Long empresaId);

  ProveedorResponse cambiarEstado(Long id, boolean activo, Long empresaId);
}