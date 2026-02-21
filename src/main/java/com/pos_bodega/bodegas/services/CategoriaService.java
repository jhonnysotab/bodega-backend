package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.CategoriaRequest;
import com.pos_bodega.bodegas.controller.dto.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

  CategoriaResponse crear(CategoriaRequest request );

  List<CategoriaResponse> listar(Long empresaId);

  List<CategoriaResponse> listarActivas(Long empresaId);

  CategoriaResponse obtener(Long id, Long empresaId);

  CategoriaResponse actualizar(Long id, CategoriaRequest request, Long empresaId);

  void cambiarEstado(Long id, Boolean activo, Long empresaId);
}