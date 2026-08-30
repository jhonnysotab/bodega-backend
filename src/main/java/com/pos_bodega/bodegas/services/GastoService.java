package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.GastoRequest;
import com.pos_bodega.bodegas.controller.dto.GastoResponse;

import java.time.LocalDate;
import java.util.List;

public interface GastoService {

  GastoResponse crear(GastoRequest request, Long empresaId, Long usuarioId);

  GastoResponse actualizar(Long id, GastoRequest request, Long empresaId);

  List<GastoResponse> listar(Long empresaId);

  List<GastoResponse> listarPorFecha(Long empresaId, LocalDate desde, LocalDate hasta);

  GastoResponse obtener(Long id, Long empresaId);

  void eliminar(Long id, Long empresaId);
}
