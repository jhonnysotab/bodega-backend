package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.EmpresaRequest;
import com.pos_bodega.bodegas.controller.dto.EmpresaResponse;
import com.pos_bodega.bodegas.services.EmpresaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Empresas", description = "Gestión de empresas (bodegas) del sistema")
public class EmpresaController {

  private final EmpresaService empresaService;

  // -----------------------------------------------
  // POST /api/empresas
  // Registrar nueva empresa
  // -----------------------------------------------
  @Operation(summary = "Crear empresa", description = "Registra una nueva bodega en el sistema")
  @PostMapping
  public ResponseEntity<EmpresaResponse> crear(@RequestBody EmpresaRequest request) {
    EmpresaResponse response = empresaService.crear(request);
    return ResponseEntity.status(201).body(response);
  }

  // -----------------------------------------------
  // GET /api/empresas
  // Listar todas las empresas
  // -----------------------------------------------
  @Operation(summary = "Listar empresas", description = "Retorna todas las empresas registradas")
  @GetMapping
  public ResponseEntity<List<EmpresaResponse>> listar() {
    return ResponseEntity.ok(empresaService.listar());
  }

  // -----------------------------------------------
  // GET /api/empresas/activas
  // Listar solo empresas activas
  // -----------------------------------------------
  @Operation(summary = "Listar empresas activas", description = "Retorna solo las empresas con estado activo")
  @GetMapping("/activas")
  public ResponseEntity<List<EmpresaResponse>> listarActivas() {
    return ResponseEntity.ok(empresaService.listarActivas());
  }

  // -----------------------------------------------
  // GET /api/empresas/{id}
  // Obtener empresa por ID
  // -----------------------------------------------
  @Operation(summary = "Obtener empresa", description = "Retorna una empresa por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<EmpresaResponse> obtener(@PathVariable Long id) {
    return ResponseEntity.ok(empresaService.obtener(id));
  }

  // -----------------------------------------------
  // PUT /api/empresas/{id}
  // Actualizar datos de la empresa
  // -----------------------------------------------
  @Operation(summary = "Actualizar empresa", description = "Actualiza los datos de una empresa existente")
  @PutMapping("/{id}")
  public ResponseEntity<EmpresaResponse> actualizar(
      @PathVariable Long id,
      @RequestBody EmpresaRequest request) {
    return ResponseEntity.ok(empresaService.actualizar(id, request));
  }

  // -----------------------------------------------
  // PATCH /api/empresas/{id}/estado?activo=false
  // Activar o desactivar empresa
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar estado",
      description = "Activa o desactiva una empresa. Usar ?activo=true o ?activo=false"
  )
  @PatchMapping("/{id}/estado")
  public ResponseEntity<Void> cambiarEstado(
      @PathVariable Long id,
      @RequestParam Boolean activo) {
    empresaService.cambiarEstado(id, activo);
    return ResponseEntity.noContent().build();
  }
}