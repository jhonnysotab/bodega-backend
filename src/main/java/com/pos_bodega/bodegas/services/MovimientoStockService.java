package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.MovimientoStockRequest;
import com.pos_bodega.bodegas.controller.dto.MovimientoStockResponse;

import java.util.List;

public interface MovimientoStockService {

  MovimientoStockResponse registrar(MovimientoStockRequest request, Long empresaId, Long usuarioId);

  List<MovimientoStockResponse> listar(Long empresaId);

  List<MovimientoStockResponse> listarPorProducto(Long productoId, Long empresaId);

  List<MovimientoStockResponse> listarPorProveedor(Long proveedorId, Long empresaId);
}
