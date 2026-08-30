package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.CajaAperturaRequest;
import com.pos_bodega.bodegas.controller.dto.CajaCierreRequest;
import com.pos_bodega.bodegas.controller.dto.CajaResponse;

import java.util.List;

public interface CajaService {

  CajaResponse abrir(CajaAperturaRequest request, Long empresaId, Long usuarioId);

  CajaResponse cerrar(CajaCierreRequest request, Long empresaId, Long usuarioId);

  CajaResponse obtenerActual(Long empresaId);

  List<CajaResponse> listar(Long empresaId);

  CajaResponse obtener(Long id, Long empresaId);
}
