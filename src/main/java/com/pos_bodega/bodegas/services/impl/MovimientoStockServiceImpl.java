package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.MovimientoStockRequest;
import com.pos_bodega.bodegas.controller.dto.MovimientoStockResponse;
import com.pos_bodega.bodegas.repository.MovimientoStockRepository;
import com.pos_bodega.bodegas.repository.ProveedorRepository;
import com.pos_bodega.bodegas.repository.entity.*;
import com.pos_bodega.bodegas.repository.ProductoRepository;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.services.MovimientoStockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoStockServiceImpl implements MovimientoStockService {

  private final MovimientoStockRepository movimientoStockRepository;
  private final ProductoRepository productoRepository;
  private final ProveedorRepository proveedorRepository;
  private final UsuarioRepository usuarioRepository;

  // -----------------------------------------------
  // REGISTRAR INGRESO O AJUSTE
  // -----------------------------------------------
  @Override
  @Transactional
  public MovimientoStockResponse registrar(MovimientoStockRequest request, Long empresaId, Long usuarioId) {

    // Validar tipo de movimiento (solo INGRESO y AJUSTE desde aquí, VENTA es automático)
    MovimientoStock.TipoMovimiento tipo;
    try {
      tipo = MovimientoStock.TipoMovimiento.valueOf(request.getTipoMovimiento().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Tipo de movimiento inválido. Use: INGRESO o AJUSTE");
    }

    if (tipo == MovimientoStock.TipoMovimiento.VENTA) {
      throw new RuntimeException("El tipo VENTA se registra automáticamente al crear una venta");
    }

    // Validar producto
    Producto producto = productoRepository.findByIdAndEmpresaId(request.getProductoId(), empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + request.getProductoId()));

    // Validar proveedor si es INGRESO
    if (tipo == MovimientoStock.TipoMovimiento.INGRESO) {
      if (request.getProveedorId() == null) {
        throw new RuntimeException("El proveedor es obligatorio para un INGRESO de stock");
      }
      proveedorRepository.findByIdAndEmpresaId(request.getProveedorId(), empresaId)
          .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + request.getProveedorId()));
    }

    // Validar cantidad
    if (request.getCantidad() == null || request.getCantidad() == 0) {
      throw new RuntimeException("La cantidad no puede ser 0");
    }

    // Calcular nuevo stock
    int stockAnterior = producto.getStock();
    int stockNuevo;

    if (tipo == MovimientoStock.TipoMovimiento.INGRESO) {
      // INGRESO: siempre suma
      stockNuevo = stockAnterior + Math.abs(request.getCantidad());
    } else {
      // AJUSTE: puede sumar o restar según el signo de la cantidad
      stockNuevo = stockAnterior + request.getCantidad();
      if (stockNuevo < 0) {
        throw new RuntimeException("El ajuste dejaría el stock en negativo. Stock actual: " + stockAnterior);
      }
    }

    // Actualizar stock del producto
    producto.setStock(stockNuevo);
    productoRepository.save(producto);

    // Guardar movimiento
    MovimientoStock mov = new MovimientoStock();
    mov.setEmpresaId(empresaId);
    mov.setProductoId(producto.getId());
    mov.setProveedorId(request.getProveedorId());
    mov.setTipoMovimiento(tipo);
    mov.setCantidad(request.getCantidad());
    mov.setStockAnterior(stockAnterior);
    mov.setStockNuevo(stockNuevo);
    mov.setNota(request.getNota());
    mov.setUsuarioId(usuarioId);

    return toResponse(movimientoStockRepository.save(mov));
  }

  // -----------------------------------------------
  // LISTAR TODOS DE LA EMPRESA
  // -----------------------------------------------
  @Override
  public List<MovimientoStockResponse> listar(Long empresaId) {
    return movimientoStockRepository.findByEmpresaIdOrderByFechaDesc(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR PRODUCTO
  // -----------------------------------------------
  @Override
  public List<MovimientoStockResponse> listarPorProducto(Long productoId, Long empresaId) {
    return movimientoStockRepository.findByProductoIdAndEmpresaIdOrderByFechaDesc(productoId, empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR PROVEEDOR
  // -----------------------------------------------
  @Override
  public List<MovimientoStockResponse> listarPorProveedor(Long proveedorId, Long empresaId) {
    return movimientoStockRepository.findByProveedorIdAndEmpresaIdOrderByFechaDesc(proveedorId, empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // HELPER
  // -----------------------------------------------
  private MovimientoStockResponse toResponse(MovimientoStock m) {
    MovimientoStockResponse res = new MovimientoStockResponse();
    res.setId(m.getId());
    res.setProductoId(m.getProductoId());
    res.setProveedorId(m.getProveedorId());
    res.setTipoMovimiento(m.getTipoMovimiento().name());
    res.setCantidad(m.getCantidad());
    res.setStockAnterior(m.getStockAnterior());
    res.setStockNuevo(m.getStockNuevo());
    res.setNota(m.getNota());
    res.setUsuarioId(m.getUsuarioId());
    res.setFecha(m.getFecha());

    // Nombre producto
    productoRepository.findById(m.getProductoId())
        .ifPresent(p -> res.setProductoNombre(p.getNombre()));

    // Nombre proveedor
    if (m.getProveedorId() != null) {
      proveedorRepository.findById(m.getProveedorId())
          .ifPresent(p -> res.setProveedorNombre(p.getNombre()));
    }

    // Nombre usuario
    usuarioRepository.findById(m.getUsuarioId())
        .ifPresent(u -> res.setUsuarioNombre(u.getNombre()));

    return res;
  }
}