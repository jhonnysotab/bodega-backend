package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.VentaRequest;
import com.pos_bodega.bodegas.controller.dto.VentaResponse;
import com.pos_bodega.bodegas.repository.entity.UsuarioRepository;
import com.pos_bodega.bodegas.services.impl.VentaServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Tag(name = "Ventas", description = "Registro y consulta de ventas")
public class VentaController {

  private final VentaServiceImpl ventaService;
  private final UsuarioRepository usuarioRepository;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  private Long getUsuarioId(HttpServletRequest request) {
    String username = request.getUserPrincipal().getName();
    return usuarioRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"))
        .getId();
  }

  @Operation(summary = "Registrar venta", description = "Crea una venta, descuenta stock automáticamente y genera número de venta")
  @PostMapping
  public ResponseEntity<VentaResponse> crear(
      @RequestBody VentaRequest ventaRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    Long usuarioId = getUsuarioId(request);
    return ResponseEntity.status(201).body(ventaService.crear(ventaRequest, empresaId, usuarioId));
  }

  @Operation(summary = "Listar ventas", description = "Lista todas las ventas de la empresa ordenadas por fecha")
  @GetMapping
  public ResponseEntity<List<VentaResponse>> listar(HttpServletRequest request) {
    return ResponseEntity.ok(ventaService.listar(getEmpresaId(request)));
  }

  @Operation(summary = "Listar por fecha", description = "Lista ventas entre dos fechas. Formato: yyyy-MM-dd'T'HH:mm:ss")
  @GetMapping("/por-fecha")
  public ResponseEntity<List<VentaResponse>> listarPorFecha(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime desde,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime hasta,
      HttpServletRequest request) {

    return ResponseEntity.ok(ventaService.listarPorFecha(getEmpresaId(request), desde, hasta));
  }

  @Operation(summary = "Obtener venta", description = "Retorna una venta con su detalle de productos")
  @GetMapping("/{id}")
  public ResponseEntity<VentaResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    return ResponseEntity.ok(ventaService.obtener(id, getEmpresaId(request)));
  }
}