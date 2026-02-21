package com.pos_bodega.bodegas.controller;

import com.pos_bodega.bodegas.controller.dto.AuthResponse;
import com.pos_bodega.bodegas.controller.dto.LoginRequest;
import com.pos_bodega.bodegas.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * POST /api/auth/login
   *
   * Body:
   * {
   *   "username": "admin",
   *   "password": "1234"
   * }
   *
   * Response:
   * {
   *   "token": "eyJhbGci...",
   *   "username": "admin",
   *   "nombre": "Administrador",
   *   "rol": "ADMIN",
   *   "empresaId": 1
   * }
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }
}