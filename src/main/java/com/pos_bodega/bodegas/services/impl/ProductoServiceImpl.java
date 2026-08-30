package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.ProductoRequest;
import com.pos_bodega.bodegas.controller.dto.ProductoResponse;
import com.pos_bodega.bodegas.repository.entity.Categoria;
import com.pos_bodega.bodegas.repository.CategoriaRepository;
import com.pos_bodega.bodegas.repository.entity.Producto;
import com.pos_bodega.bodegas.repository.ProductoRepository;
import com.pos_bodega.bodegas.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

  private final ProductoRepository productoRepository;
  private final CategoriaRepository categoriaRepository;

  // -----------------------------------------------
  // CREAR
  // -----------------------------------------------
  @Override
  public ProductoResponse crear(ProductoRequest request, Long empresaId) {

    // Validar precio de venta obligatorio
    if (request.getPrecioVenta() == null || request.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("El precio de venta es obligatorio y debe ser mayor a 0");
    }

    // Validar código de barras único si viene con valor
    if (request.getCodigoBarras() != null && !request.getCodigoBarras().isBlank()) {
      if (productoRepository.existsByCodigoBarrasAndEmpresaId(request.getCodigoBarras(), empresaId)) {
        throw new RuntimeException("Ya existe un producto con el código de barras: " + request.getCodigoBarras());
      }
    }

    // Validar que la categoría existe y pertenece a la empresa
    if (request.getCategoriaId() != null) {
      categoriaRepository.findByIdAndEmpresaId(request.getCategoriaId(), empresaId)
          .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));
    }

    Producto producto = new Producto();
    producto.setEmpresaId(empresaId);
    mapRequestToEntity(request, producto);
    producto.setActivo(true);

    return toResponse(productoRepository.save(producto));
  }

  // -----------------------------------------------
  // LISTAR TODOS
  // -----------------------------------------------
  @Override
  public List<ProductoResponse> listar(Long empresaId) {
    return productoRepository.findByEmpresaId(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR SOLO ACTIVOS
  // -----------------------------------------------
  @Override
  public List<ProductoResponse> listarActivos(Long empresaId) {
    return productoRepository.findByEmpresaIdAndActivoTrue(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR CATEGORÍA
  // -----------------------------------------------
  @Override
  public List<ProductoResponse> listarPorCategoria(Long categoriaId, Long empresaId) {
    return productoRepository.findByCategoriaIdAndEmpresaId(categoriaId, empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR STOCK BAJO
  // -----------------------------------------------
  @Override
  public List<ProductoResponse> listarStockBajo(Long empresaId) {
    return productoRepository.findProductosStockBajo(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // BUSCAR POR NOMBRE
  // -----------------------------------------------
  @Override
  public List<ProductoResponse> buscarPorNombre(String nombre, Long empresaId) {
    return productoRepository
        .findByEmpresaIdAndNombreContainingIgnoreCaseAndActivoTrue(empresaId, nombre)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public ProductoResponse obtener(Long id, Long empresaId) {
    return toResponse(findByIdAndEmpresa(id, empresaId));
  }

  // -----------------------------------------------
  // OBTENER POR CÓDIGO DE BARRAS
  // -----------------------------------------------
  @Override
  public ProductoResponse obtenerPorCodigoBarras(String codigoBarras, Long empresaId) {
    Producto producto = productoRepository
        .findByCodigoBarrasAndEmpresaId(codigoBarras, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + codigoBarras));
    return toResponse(producto);
  }

  // -----------------------------------------------
  // ACTUALIZAR
  // -----------------------------------------------
  @Override
  public ProductoResponse actualizar(Long id, ProductoRequest request, Long empresaId) {
    Producto producto = findByIdAndEmpresa(id, empresaId);

    // Validar precio de venta
    if (request.getPrecioVenta() == null || request.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("El precio de venta debe ser mayor a 0");
    }

    // Validar código de barras único si cambió
    if (request.getCodigoBarras() != null && !request.getCodigoBarras().isBlank()) {
      if (!request.getCodigoBarras().equals(producto.getCodigoBarras())) {
        if (productoRepository.existsByCodigoBarrasAndEmpresaId(request.getCodigoBarras(), empresaId)) {
          throw new RuntimeException("Ya existe un producto con el código de barras: " + request.getCodigoBarras());
        }
      }
    }

    // Validar categoría si cambió
    if (request.getCategoriaId() != null) {
      categoriaRepository.findByIdAndEmpresaId(request.getCategoriaId(), empresaId)
          .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId()));
    }

    mapRequestToEntity(request, producto);
    return toResponse(productoRepository.save(producto));
  }

  // -----------------------------------------------
  // CAMBIAR ESTADO
  // -----------------------------------------------
  @Override
  public void cambiarEstado(Long id, Boolean activo, Long empresaId) {
    Producto producto = findByIdAndEmpresa(id, empresaId);
    producto.setActivo(activo);
    productoRepository.save(producto);
  }

  // -----------------------------------------------
  // HELPERS PRIVADOS
  // -----------------------------------------------
  private Producto findByIdAndEmpresa(Long id, Long empresaId) {
    return productoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
  }

  private void mapRequestToEntity(ProductoRequest request, Producto producto) {
    producto.setNombre(request.getNombre().trim());
    producto.setCodigoBarras(request.getCodigoBarras());
    producto.setPrecioCompra(request.getPrecioCompra());
    producto.setPrecioVenta(request.getPrecioVenta());
    producto.setStock(request.getStock() != null ? request.getStock() : 0);
    producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 0);
    producto.setCategoriaId(request.getCategoriaId());
  }

  private ProductoResponse toResponse(Producto p) {
    ProductoResponse res = new ProductoResponse();
    res.setId(p.getId());
    res.setNombre(p.getNombre());
    res.setCodigoBarras(p.getCodigoBarras());
    res.setPrecioCompra(p.getPrecioCompra());
    res.setPrecioVenta(p.getPrecioVenta());
    res.setStock(p.getStock());
    res.setStockMinimo(p.getStockMinimo());
    res.setActivo(p.getActivo());
    res.setEmpresaId(p.getEmpresaId());
    res.setCategoriaId(p.getCategoriaId());
    res.setFechaCreacion(p.getFechaCreacion());

    // Stock bajo: true si stock <= stockMinimo
    res.setStockBajo(p.getStock() != null && p.getStockMinimo() != null
        && p.getStock() <= p.getStockMinimo());

    // Calcular margen de ganancia %
    if (p.getPrecioCompra() != null && p.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0
        && p.getPrecioVenta() != null) {
      BigDecimal margen = p.getPrecioVenta()
          .subtract(p.getPrecioCompra())
          .multiply(BigDecimal.valueOf(100))
          .divide(p.getPrecioCompra(), 2, RoundingMode.HALF_UP);
      res.setMargenGanancia(margen);
    }

    // Nombre de categoría
    if (p.getCategoriaId() != null) {
      categoriaRepository.findById(p.getCategoriaId())
          .map(Categoria::getNombre)
          .ifPresent(res::setCategoriaNombre);
    }

    return res;
  }
}