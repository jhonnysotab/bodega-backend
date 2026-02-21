package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.ProveedorRequest;
import com.pos_bodega.bodegas.controller.dto.ProveedorResponse;
import com.pos_bodega.bodegas.services.ProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
@Tag(name = "Proveedores", description = "Gestión de proveedores de la bodega")
public class ProveedorController {

  private final ProveedorService proveedorService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  // -----------------------------------------------
  // POST /api/proveedores
  // -----------------------------------------------
  @Operation(summary = "Crear proveedor", description = "Registra un nuevo proveedor con sus categorías")
  @PostMapping
  public ResponseEntity<ProveedorResponse> crear(
      @RequestBody ProveedorRequest request,
      HttpServletRequest httpRequest) {

    Long empresaId = getEmpresaId(httpRequest);
    return ResponseEntity.status(201).body(proveedorService.crear(request, empresaId));
  }

  // -----------------------------------------------
  // GET /api/proveedores
  // -----------------------------------------------
  @Operation(summary = "Listar proveedores", description = "Retorna todos los proveedores de la empresa")
  @GetMapping
  public ResponseEntity<List<ProveedorResponse>> listar(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(proveedorService.listar(empresaId));
  }

  // -----------------------------------------------
  // GET /api/proveedores/activos
  // -----------------------------------------------
  @Operation(summary = "Listar activos", description = "Retorna solo los proveedores activos")
  @GetMapping("/activos")
  public ResponseEntity<List<ProveedorResponse>> listarActivos(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(proveedorService.listarActivos(empresaId));
  }

  // -----------------------------------------------
  // GET /api/proveedores/{id}
  // -----------------------------------------------
  @Operation(summary = "Obtener proveedor", description = "Retorna un proveedor con sus categorías por ID")
  @GetMapping("/{id}")
  public ResponseEntity<ProveedorResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(proveedorService.obtener(id, empresaId));
  }

  // -----------------------------------------------
  // PUT /api/proveedores/{id}
  // -----------------------------------------------
  @Operation(summary = "Actualizar proveedor", description = "Actualiza datos y reasigna categorías del proveedor")
  @PutMapping("/{id}")
  public ResponseEntity<ProveedorResponse> actualizar(
      @PathVariable Long id,
      @RequestBody ProveedorRequest request,
      HttpServletRequest httpRequest) {

    Long empresaId = getEmpresaId(httpRequest);
    return ResponseEntity.ok(proveedorService.actualizar(id, request, empresaId));
  }

  // -----------------------------------------------
  // PATCH /api/proveedores/{id}/estado?activo=false
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar estado",
      description = "Activa o desactiva un proveedor. Usar ?activo=true o ?activo=false"
  )
  @PatchMapping("/{id}/estado")
  public ResponseEntity<Void> cambiarEstado(
      @PathVariable Long id,
      @RequestParam Boolean activo,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    proveedorService.cambiarEstado(id, activo, empresaId);
    return ResponseEntity.noContent().build();
  }
}