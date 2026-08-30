package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.GastoRequest;
import com.pos_bodega.bodegas.controller.dto.GastoResponse;
import com.pos_bodega.bodegas.services.GastoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
@Tag(name = "Gastos", description = "Registro de gastos del negocio")
public class GastoController {

  private final GastoService gastoService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  private Long getUsuarioId(HttpServletRequest request) {
    return (Long) request.getAttribute("usuarioId");
  }

  @Operation(summary = "Registrar gasto", description = "Registra un nuevo gasto del negocio")
  @PostMapping
  public ResponseEntity<GastoResponse> crear(
      @RequestBody GastoRequest gastoRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    Long usuarioId = getUsuarioId(request);
    return ResponseEntity.status(201).body(gastoService.crear(gastoRequest, empresaId, usuarioId));
  }

  @Operation(summary = "Actualizar gasto", description = "Actualiza un gasto existente")
  @PutMapping("/{id}")
  public ResponseEntity<GastoResponse> actualizar(
      @PathVariable Long id,
      @RequestBody GastoRequest gastoRequest,
      HttpServletRequest request) {

    return ResponseEntity.ok(gastoService.actualizar(id, gastoRequest, getEmpresaId(request)));
  }

  @Operation(summary = "Listar gastos", description = "Lista todos los gastos de la empresa")
  @GetMapping
  public ResponseEntity<List<GastoResponse>> listar(HttpServletRequest request) {
    return ResponseEntity.ok(gastoService.listar(getEmpresaId(request)));
  }

  @Operation(summary = "Por fecha", description = "Lista gastos entre dos fechas. Formato: yyyy-MM-dd")
  @GetMapping("/por-fecha")
  public ResponseEntity<List<GastoResponse>> listarPorFecha(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
      HttpServletRequest request) {

    return ResponseEntity.ok(gastoService.listarPorFecha(getEmpresaId(request), desde, hasta));
  }

  @Operation(summary = "Obtener gasto", description = "Retorna un gasto por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<GastoResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    return ResponseEntity.ok(gastoService.obtener(id, getEmpresaId(request)));
  }

  @Operation(summary = "Eliminar gasto", description = "Elimina un gasto registrado")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(
      @PathVariable Long id,
      HttpServletRequest request) {

    gastoService.eliminar(id, getEmpresaId(request));
    return ResponseEntity.noContent().build();
  }
}




