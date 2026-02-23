package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.CategoriaRequest;
import com.pos_bodega.bodegas.controller.dto.CategoriaResponse;
import com.pos_bodega.bodegas.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categorías", description = "Gestión de categorías de productos")
public class CategoriaController {

  private final CategoriaService categoriaService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  // -----------------------------------------------
  // POST /api/categorias
  // -----------------------------------------------
  @Operation(summary = "Crear categoría", description = "Crea una nueva categoría para la empresa")
  @PostMapping
  public ResponseEntity<CategoriaResponse> crear(
      @RequestBody CategoriaRequest request,HttpServletRequest request2) {
    Long empresaId = getEmpresaId(request2);
    return ResponseEntity.status(201).body(categoriaService.crear(request,empresaId));
  }

  // -----------------------------------------------
  // GET /api/categorias
  // -----------------------------------------------
  @Operation(summary = "Listar categorías", description = "Retorna todas las categorías de la empresa")
  @GetMapping
  public ResponseEntity<List<CategoriaResponse>> listar(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(categoriaService.listar(empresaId));
  }

  // -----------------------------------------------
  // GET /api/categorias/activas
  // -----------------------------------------------
  @Operation(summary = "Listar activas", description = "Retorna solo las categorías activas de la empresa")
  @GetMapping("/activas")
  public ResponseEntity<List<CategoriaResponse>> listarActivas(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(categoriaService.listarActivas(empresaId));
  }

  // -----------------------------------------------
  // GET /api/categorias/{id}
  // -----------------------------------------------
  @Operation(summary = "Obtener categoría", description = "Retorna una categoría por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<CategoriaResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(categoriaService.obtener(id, empresaId));
  }

  // -----------------------------------------------
  // PUT /api/categorias/{id}
  // -----------------------------------------------
  @Operation(summary = "Actualizar categoría", description = "Actualiza nombre y descripción de la categoría")
  @PutMapping("/{id}")
  public ResponseEntity<CategoriaResponse> actualizar(
      @PathVariable Long id,
      @RequestBody CategoriaRequest request,
      HttpServletRequest httpRequest) {

    Long empresaId = getEmpresaId(httpRequest);
    return ResponseEntity.ok(categoriaService.actualizar(id, request, empresaId));
  }

  // -----------------------------------------------
  // PATCH /api/categorias/{id}/estado?activo=false
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar estado",
      description = "Activa o desactiva una categoría. Usar ?activo=true o ?activo=false"
  )
  @PatchMapping("/{id}/estado")
  public ResponseEntity<Void> cambiarEstado(
      @PathVariable Long id,
      @RequestParam Boolean activo,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    categoriaService.cambiarEstado(id, activo, empresaId);
    return ResponseEntity.noContent().build();
  }
}