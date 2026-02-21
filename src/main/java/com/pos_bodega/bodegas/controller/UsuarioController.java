package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.CambiarPasswordRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioResponse;
import com.pos_bodega.bodegas.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
public class UsuarioController {

  private final UsuarioService usuarioService;

  // Helper: obtiene empresaId desde el JWT (puesto por JwtFilter)
  private Long getEmpresaId(HttpServletRequest request) {
    return (Long) request.getAttribute("empresaId");
  }

  // -----------------------------------------------
  // POST /api/usuarios
  // Crear nuevo usuario
  // -----------------------------------------------
  @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario para la empresa")
  @PostMapping
  public ResponseEntity<UsuarioResponse> crear(
      @RequestBody UsuarioRequest usuarioRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    UsuarioResponse response = usuarioService.crear(usuarioRequest, empresaId);
    return ResponseEntity.status(201).body(response);
  }

  // -----------------------------------------------
  // GET /api/usuarios
  // Listar todos los usuarios de la empresa
  // -----------------------------------------------
  @Operation(summary = "Listar usuarios", description = "Retorna todos los usuarios de la empresa")
  @GetMapping()
  public ResponseEntity<List<UsuarioResponse>> listar(  HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(usuarioService.listar(empresaId));
  }

  // -----------------------------------------------
  // GET /api/usuarios/{id}
  // Obtener un usuario por ID
  // -----------------------------------------------
  @Operation(summary = "Obtener usuario", description = "Retorna un usuario por su ID")
  @GetMapping("/{id}")
  public ResponseEntity<UsuarioResponse> obtener(
      @PathVariable Long id,  HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(usuarioService.obtener(id, empresaId));
  }

  // -----------------------------------------------
  // PUT /api/usuarios/{id}
  // Actualizar datos del usuario
  // -----------------------------------------------
  @Operation(summary = "Actualizar usuario", description = "Actualiza nombre, username y rol del usuario")
  @PutMapping("/{id}")
  public ResponseEntity<UsuarioResponse> actualizar(
      @PathVariable Long id,
      @RequestBody UsuarioRequest usuarioRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    return ResponseEntity.ok(usuarioService.actualizar(id, usuarioRequest,empresaId));
  }

  // -----------------------------------------------
  // PATCH /api/usuarios/{id}/estado?activo=false
  // Activar o desactivar usuario (eliminar por estado)
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar estado",
      description = "Activa o desactiva un usuario. Usar ?activo=true o ?activo=false"
  )
  @PatchMapping("/{id}/{idempresa}/estado")
  public ResponseEntity<Void> cambiarEstado(
      @PathVariable Long id,
      @PathVariable Long idempresa,
      @RequestParam Boolean activo
      ) {
    usuarioService.cambiarEstado(id, activo, idempresa);
    return ResponseEntity.noContent().build();
  }

  // -----------------------------------------------
  // PATCH /api/usuarios/{id}/password
  // Cambiar contraseña del usuario
  // -----------------------------------------------
  @Operation(
      summary = "Cambiar contraseña",
      description = "Permite al usuario cambiar su propia contraseña"
  )
  @PatchMapping("/{id}/password")
  public ResponseEntity<Void> cambiarPassword(
      @PathVariable Long id,
      @RequestBody CambiarPasswordRequest passwordRequest,
      HttpServletRequest request) {

    Long empresaId = getEmpresaId(request);
    usuarioService.cambiarPassword(id, passwordRequest, empresaId);
    return ResponseEntity.noContent().build();
  }
}