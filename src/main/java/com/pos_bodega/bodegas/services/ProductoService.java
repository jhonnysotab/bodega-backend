package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.ProductoRequest;
import com.pos_bodega.bodegas.controller.dto.ProductoResponse;

import java.util.List;

public interface ProductoService {

  ProductoResponse crear(ProductoRequest request, Long empresaId);

  List<ProductoResponse> listar(Long empresaId);

  List<ProductoResponse> listarActivos(Long empresaId);

  List<ProductoResponse> listarPorCategoria(Long categoriaId, Long empresaId);

  List<ProductoResponse> listarStockBajo(Long empresaId);

  List<ProductoResponse> buscarPorNombre(String nombre, Long empresaId);

  ProductoResponse obtener(Long id, Long empresaId);

  ProductoResponse obtenerPorCodigoBarras(String codigoBarras, Long empresaId);

  ProductoResponse actualizar(Long id, ProductoRequest request, Long empresaId);

  void cambiarEstado(Long id, Boolean activo, Long empresaId);
}