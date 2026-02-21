package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.CambiarPasswordRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioResponse;

import java.util.List;

public interface UsuarioService {

  // Crear nuevo usuario
  UsuarioResponse crear(UsuarioRequest request, Long empresaId);

  // Listar todos los usuarios de la empresa
  List<UsuarioResponse> listar(Long empresaId);

  // Obtener uno por ID
  UsuarioResponse obtener(Long id, Long empresaId);

  // Actualizar datos del usuario
  UsuarioResponse actualizar(Long id, UsuarioRequest request, Long empresaId);

  // Cambiar estado activo/inactivo
  void cambiarEstado(Long id, Boolean activo, Long empresaId);

  // Cambiar contraseña (el propio usuario)
  void cambiarPassword(Long id, CambiarPasswordRequest request, Long empresaId);
}