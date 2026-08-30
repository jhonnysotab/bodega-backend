package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.CajaAperturaRequest;
import com.pos_bodega.bodegas.controller.dto.CajaCierreRequest;
import com.pos_bodega.bodegas.controller.dto.CajaResponse;
import com.pos_bodega.bodegas.services.CajaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
@Tag(name = "Caja", description = "Apertura, cierre y arqueo de caja")
public class CajaController {

  private final CajaService cajaService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  private Long getUsuarioId(HttpServletRequest request) {
    return (Long) request.getAttribute("usuarioId");
  }

  @Operation(summary = "Abrir caja", description = "Abre un nuevo turno de caja con el monto inicial en efectivo")
  @PostMapping("/abrir")
  public ResponseEntity<CajaResponse> abrir(
      @RequestBody CajaAperturaRequest request,
      HttpServletRequest httpRequest) {

    CajaResponse response = cajaService.abrir(request, getEmpresaId(httpRequest), getUsuarioId(httpRequest));
    return ResponseEntity.status(201).body(response);
  }

  @Operation(summary = "Cerrar caja", description = "Cierra el turno de caja actualmente abierto y calcula la diferencia de arqueo")
  @PostMapping("/cerrar")
  public ResponseEntity<CajaResponse> cerrar(
      @RequestBody CajaCierreRequest request,
      HttpServletRequest httpRequest) {

    CajaResponse response = cajaService.cerrar(request, getEmpresaId(httpRequest), getUsuarioId(httpRequest));
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Caja actual", description = "Retorna el turno de caja abierto (con el resumen en vivo) o null si no hay ninguno")
  @GetMapping("/actual")
  public ResponseEntity<CajaResponse> actual(HttpServletRequest request) {
    return ResponseEntity.ok(cajaService.obtenerActual(getEmpresaId(request)));
  }

  @Operation(summary = "Historial de caja", description = "Lista los turnos de caja de la empresa, más reciente primero")
  @GetMapping
  public ResponseEntity<List<CajaResponse>> listar(HttpServletRequest request) {
    return ResponseEntity.ok(cajaService.listar(getEmpresaId(request)));
  }

  @Operation(summary = "Obtener turno de caja", description = "Retorna el detalle de un turno de caja por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<CajaResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    return ResponseEntity.ok(cajaService.obtener(id, getEmpresaId(request)));
  }
}
