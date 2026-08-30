package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.VentaRequest;
import com.pos_bodega.bodegas.controller.dto.VentaResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface VentaService {

  VentaResponse crear(VentaRequest request, Long empresaId, Long usuarioId);

  List<VentaResponse> listar(Long empresaId);

  List<VentaResponse> listarPorFecha(Long empresaId, LocalDateTime desde, LocalDateTime hasta);

  VentaResponse obtener(Long id, Long empresaId);
}
