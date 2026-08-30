package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.MovimientoStockRequest;
import com.pos_bodega.bodegas.controller.dto.MovimientoStockResponse;
import com.pos_bodega.bodegas.services.MovimientoStockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimientos-stock")
@RequiredArgsConstructor
@Tag(name = "Movimientos de Stock", description = "Ingresos de mercadería por proveedor y ajustes de inventario")
public class MovimientoStockController {

  private final MovimientoStockService movimientoService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  private Long getUsuarioId(HttpServletRequest request) {
    return (Long) request.getAttribute("usuarioId");
  }

  @Operation(summary = "Registrar movimiento", description = "Registra un INGRESO (con proveedor) o AJUSTE de stock")
  @PostMapping
  public ResponseEntity<MovimientoStockResponse> registrar(
      @RequestBody MovimientoStockRequest movRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    Long usuarioId = getUsuarioId(request);
    return ResponseEntity.status(201).body(movimientoService.registrar(movRequest, empresaId, usuarioId));
  }

  @Operation(summary = "Listar movimientos", description = "Lista todos los movimientos de stock de la empresa")
  @GetMapping
  public ResponseEntity<List<MovimientoStockResponse>> listar(HttpServletRequest request) {
    return ResponseEntity.ok(movimientoService.listar(getEmpresaId(request)));
  }

  @Operation(summary = "Por producto", description = "Lista el historial de movimientos de un producto")
  @GetMapping("/producto/{productoId}")
  public ResponseEntity<List<MovimientoStockResponse>> listarPorProducto(
      @PathVariable Long productoId,
      HttpServletRequest request) {

    return ResponseEntity.ok(movimientoService.listarPorProducto(productoId, getEmpresaId(request)));
  }

  @Operation(summary = "Por proveedor", description = "Lista todos los ingresos realizados por un proveedor")
  @GetMapping("/proveedor/{proveedorId}")
  public ResponseEntity<List<MovimientoStockResponse>> listarPorProveedor(
      @PathVariable Long proveedorId,
      HttpServletRequest request) {

    return ResponseEntity.ok(movimientoService.listarPorProveedor(proveedorId, getEmpresaId(request)));
  }
}