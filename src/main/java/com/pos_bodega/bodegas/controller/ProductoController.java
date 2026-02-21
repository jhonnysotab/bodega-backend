package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.ProductoRequest;
import com.pos_bodega.bodegas.controller.dto.ProductoResponse;
import com.pos_bodega.bodegas.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {

  private final ProductoService productoService;

  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  // -----------------------------------------------
  // POST /api/productos
  // -----------------------------------------------
  @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el catálogo")
  @PostMapping
  public ResponseEntity<ProductoResponse> crear(
      @RequestBody ProductoRequest request,
      HttpServletRequest httpRequest) {

    Long empresaId = getEmpresaId(httpRequest);
    return ResponseEntity.status(201).body(productoService.crear(request, empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos
  // -----------------------------------------------
  @Operation(summary = "Listar productos", description = "Retorna todos los productos de la empresa")
  @GetMapping
  public ResponseEntity<List<ProductoResponse>> listar(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.listar(empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/activos
  // -----------------------------------------------
  @Operation(summary = "Listar activos", description = "Retorna solo los productos activos")
  @GetMapping("/activos")
  public ResponseEntity<List<ProductoResponse>> listarActivos(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.listarActivos(empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/stock-bajo
  // -----------------------------------------------
  @Operation(summary = "Stock bajo", description = "Retorna productos cuyo stock es menor o igual al stock mínimo")
  @GetMapping("/stock-bajo")
  public ResponseEntity<List<ProductoResponse>> listarStockBajo(HttpServletRequest request) {
    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.listarStockBajo(empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/buscar?nombre=coca
  // -----------------------------------------------
  @Operation(summary = "Buscar por nombre", description = "Busca productos activos que contengan el nombre")
  @GetMapping("/buscar")
  public ResponseEntity<List<ProductoResponse>> buscarPorNombre(
      @RequestParam String nombre,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.buscarPorNombre(nombre, empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/categoria/{categoriaId}
  // -----------------------------------------------
  @Operation(summary = "Listar por categoría", description = "Retorna productos de una categoría específica")
  @GetMapping("/categoria/{categoriaId}")
  public ResponseEntity<List<ProductoResponse>> listarPorCategoria(
      @PathVariable Long categoriaId,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.listarPorCategoria(categoriaId, empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/{id}
  // -----------------------------------------------
  @Operation(summary = "Obtener producto", description = "Retorna un producto por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<ProductoResponse> obtener(
      @PathVariable Long id,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.obtener(id, empresaId));
  }

  // -----------------------------------------------
  // GET /api/productos/codigo/{codigoBarras}
  // -----------------------------------------------
  @Operation(summary = "Buscar por código de barras", description = "Busca un producto por su código de barras (útil para escáner)")
  @GetMapping("/codigo/{codigoBarras}")
  public ResponseEntity<ProductoResponse> obtenerPorCodigo(
      @PathVariable String codigoBarras,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(productoService.obtenerPorCodigoBarras(codigoBarras, empresaId));
  }

  // -----------------------------------------------
  // PUT /api/productos/{id}
  // -----------------------------------------------
  @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente")
  @PutMapping("/{id}")
  public ResponseEntity<ProductoResponse> actualizar(
      @PathVariable Long id,
      @RequestBody ProductoRequest request,
      HttpServletRequest httpRequest) {

    Long empresaId = getEmpresaId(httpRequest);
    return ResponseEntity.ok(productoService.actualizar(id, request, empresaId));
  }

  // -----------------------------------------------
  // PATCH /api/productos/{id}/estado?activo=false
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar estado",
      description = "Activa o desactiva un producto. Usar ?activo=true o ?activo=false"
  )
  @PatchMapping("/{id}/estado")
  public ResponseEntity<Void> cambiarEstado(
      @PathVariable Long id,
      @RequestParam Boolean activo,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    productoService.cambiarEstado(id, activo, empresaId);
    return ResponseEntity.ok().build();
  }
}