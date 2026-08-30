package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.BusinessException;
import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.*;
import com.pos_bodega.bodegas.repository.CajaRepository;
import com.pos_bodega.bodegas.repository.MovimientoStockRepository;
import com.pos_bodega.bodegas.repository.VentaDetalleRepository;
import com.pos_bodega.bodegas.repository.VentaRepository;
import com.pos_bodega.bodegas.repository.entity.*;
import com.pos_bodega.bodegas.repository.ProductoRepository;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.services.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

  private final VentaRepository ventaRepository;
  private final VentaDetalleRepository ventaDetalleRepository;
  private final ProductoRepository productoRepository;
  private final MovimientoStockRepository movimientoStockRepository;
  private final UsuarioRepository usuarioRepository;
  private final CajaRepository cajaRepository;

  // -----------------------------------------------
  // CREAR VENTA - lógica crítica
  // -----------------------------------------------
  @Override
  @Transactional
  public VentaResponse crear(VentaRequest request, Long empresaId, Long usuarioId) {

    // 1. Validar que venga al menos un producto
    if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
      throw new RuntimeException("La venta debe tener al menos un producto");
    }

    // 1.1 Debe haber una caja abierta para poder vender
    Caja caja = cajaRepository.findByEmpresaIdAndEstado(empresaId, Caja.Estado.ABIERTA)
        .orElseThrow(() -> new BusinessException("Debes abrir la caja antes de registrar una venta"));

    // 2. Validar tipo de pago
    Venta.TipoPago tipoPago;
    try {
      tipoPago = Venta.TipoPago.valueOf(request.getTipoPago().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Tipo de pago inválido. Use: EFECTIVO, YAPE, PLIN, TRANSFERENCIA");
    }

    // 3. Validar productos y stock
    List<Producto> productosVenta = new ArrayList<>();
    for (VentaDetalleRequest detalle : request.getDetalles()) {
      Producto producto = productoRepository
          .findByIdAndEmpresaId(detalle.getProductoId(), empresaId)
          .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + detalle.getProductoId()));

      if (!producto.getActivo() ) {
        throw new RuntimeException("El producto '" + producto.getNombre() + "' no está activo");
      }

      if (producto.getStock() < detalle.getCantidad()) {
        throw new RuntimeException("Stock insuficiente para '" + producto.getNombre()
            + "'. Stock actual: " + producto.getStock());
      }
      productosVenta.add(producto);
    }

    // 4. Calcular total
    BigDecimal total = BigDecimal.ZERO;
    for (int i = 0; i < request.getDetalles().size(); i++) {
      VentaDetalleRequest d = request.getDetalles().get(i);
      BigDecimal subtotal = productosVenta.get(i).getPrecioVenta()
          .multiply(BigDecimal.valueOf(d.getCantidad()));
      total = total.add(subtotal);
    }

    // 5. Validar monto recibido si es EFECTIVO
    BigDecimal vuelto = BigDecimal.ZERO;
    if (tipoPago == Venta.TipoPago.EFECTIVO && request.getMontoRecibido() != null) {
      if (request.getMontoRecibido().compareTo(total) < 0) {
        throw new RuntimeException("El monto recibido es menor al total de la venta");
      }
      vuelto = request.getMontoRecibido().subtract(total);
    }

    // 6. Guardar venta
    Venta venta = new Venta();
    venta.setEmpresaId(empresaId);
    venta.setCajaId(caja.getId());
    venta.setNumeroVenta(generarNumeroVenta(empresaId));
    venta.setTotal(total);
    venta.setTipoPago(tipoPago);
    venta.setMontoRecibido(request.getMontoRecibido());
    venta.setVuelto(vuelto);
    venta.setNota(request.getNota());
    venta.setUsuarioId(usuarioId);
    Venta ventaGuardada = ventaRepository.save(venta);

    // 7. Guardar detalles + descontar stock + registrar movimiento
    List<VentaDetalle> detalles = new ArrayList<>();
    for (int i = 0; i < request.getDetalles().size(); i++) {
      VentaDetalleRequest dr = request.getDetalles().get(i);
      Producto producto = productosVenta.get(i);

      // Detalle
      VentaDetalle detalle = new VentaDetalle();
      detalle.setVenta(ventaGuardada);
      detalle.setProducto(producto);
      detalle.setCantidad(dr.getCantidad());
      detalle.setPrecioUnitario(producto.getPrecioVenta());
      detalle.setSubtotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(dr.getCantidad())));
      detalles.add(detalle);

      // Descontar stock
      int stockAnterior = producto.getStock();
      producto.setStock(stockAnterior - dr.getCantidad());
      productoRepository.save(producto);

      // Registrar movimiento automático
      MovimientoStock mov = new MovimientoStock();
      mov.setEmpresaId(empresaId);
      mov.setProductoId(producto.getId());
      mov.setTipoMovimiento(MovimientoStock.TipoMovimiento.VENTA);
      mov.setCantidad(dr.getCantidad());
      mov.setStockAnterior(stockAnterior);
      mov.setStockNuevo(producto.getStock());
      mov.setNota("Venta #" + ventaGuardada.getNumeroVenta());
      mov.setUsuarioId(usuarioId);
      movimientoStockRepository.save(mov);
    }
    ventaDetalleRepository.saveAll(detalles);

    return toResponse(ventaGuardada, detalles);
  }

  // -----------------------------------------------
  // LISTAR
  // -----------------------------------------------
  @Override
  public List<VentaResponse> listar(Long empresaId) {
    return ventaRepository.findByEmpresaIdOrderByFechaVentaDesc(empresaId)
        .stream().map(v -> toResponse(v, v.getDetalles())).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR FECHA
  // -----------------------------------------------
  @Override
  public List<VentaResponse> listarPorFecha(Long empresaId, LocalDateTime desde, LocalDateTime hasta) {
    return ventaRepository.findByEmpresaIdAndFechaVentaBetween(empresaId, desde, hasta)
        .stream().map(v -> toResponse(v, v.getDetalles())).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public VentaResponse obtener(Long id, Long empresaId) {
    Venta venta = ventaRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    return toResponse(venta, venta.getDetalles());
  }

  // -----------------------------------------------
  // HELPERS
  // -----------------------------------------------
  private String generarNumeroVenta(Long empresaId) {
    String prefijo = "V-" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now()) + "-";
    return ventaRepository.findUltimoNumeroVenta(empresaId)
        .map(ultimo -> {
          String[] partes = ultimo.split("-");
          int siguiente = Integer.parseInt(partes[partes.length - 1]) + 1;
          return prefijo + String.format("%04d", siguiente);
        })
        .orElse(prefijo + "0001");
  }

  private VentaResponse toResponse(Venta v, List<VentaDetalle> detalles) {
    VentaResponse res = new VentaResponse();
    res.setId(v.getId());
    res.setCajaId(v.getCajaId());
    res.setNumeroVenta(v.getNumeroVenta());
    res.setTotal(v.getTotal());
    res.setTipoPago(v.getTipoPago().name());
    res.setMontoRecibido(v.getMontoRecibido());
    res.setVuelto(v.getVuelto());
    res.setNota(v.getNota());
    res.setUsuarioId(v.getUsuarioId());
    res.setFechaVenta(v.getFechaVenta());

    // Nombre del usuario
    usuarioRepository.findById(v.getUsuarioId())
        .ifPresent(u -> res.setUsuarioNombre(u.getNombre()));

    // Detalles
    if (detalles != null) {
      res.setDetalles(detalles.stream().map(d -> {
        VentaDetalleResponse dr = new VentaDetalleResponse();
        dr.setProductoId(d.getProducto().getId());
        dr.setProductoNombre(d.getProducto().getNombre());
        dr.setCantidad(d.getCantidad());
        dr.setPrecioUnitario(d.getPrecioUnitario());
        dr.setSubtotal(d.getSubtotal());
        return dr;
      }).collect(Collectors.toList()));
    }

    return res;
  }
}