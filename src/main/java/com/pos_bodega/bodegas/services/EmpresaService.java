package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.EmpresaRequest;
import com.pos_bodega.bodegas.controller.dto.EmpresaResponse;

import java.util.List;

public interface EmpresaService {

  EmpresaResponse crear(EmpresaRequest request);

  List<EmpresaResponse> listar();

  List<EmpresaResponse> listarActivas();

  EmpresaResponse obtener(Long id);

  EmpresaResponse actualizar(Long id, EmpresaRequest request);

  void cambiarEstado(Long id, Boolean activo);
}