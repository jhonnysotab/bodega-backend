package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.CambiarPasswordRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioRequest;
import com.pos_bodega.bodegas.controller.dto.UsuarioResponse;
import com.pos_bodega.bodegas.repository.entity.Usuario;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  // -----------------------------------------------
  // CREAR
  // -----------------------------------------------
  @Override
  public UsuarioResponse crear(UsuarioRequest request, Long empresaId) {

    // Validar username único
    if (usuarioRepository.existsByUsername(request.getUsername())) {
      throw new RuntimeException("El username '" + request.getUsername() + "' ya está en uso");
    }

    // Validar rol válido
    Usuario.Rol rol;
    try {
      rol = Usuario.Rol.valueOf(request.getRol().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Rol inválido. Use: ADMIN o CAJERO");
    }

    Usuario usuario = new Usuario();
    usuario.setEmpresaId(empresaId);
    usuario.setNombre(request.getNombre());
    usuario.setUsername(request.getUsername().toLowerCase().trim());
    usuario.setPassword(passwordEncoder.encode(request.getPassword()));
    usuario.setRol(rol);
    usuario.setActivo(true);

    return toResponse(usuarioRepository.save(usuario));
  }

  // -----------------------------------------------
  // LISTAR
  // -----------------------------------------------
  @Override
  public List<UsuarioResponse> listar(Long empresaId) {
    // ⚠️ MULTI-TENANT: solo usuarios de esta empresa
    return usuarioRepository.findByEmpresaId(empresaId)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER UNO
  // -----------------------------------------------
  @Override
  public UsuarioResponse obtener(Long id, Long empresaId) {
    Usuario usuario = findByIdAndEmpresa(id, empresaId);
    return toResponse(usuario);
  }

  // -----------------------------------------------
  // ACTUALIZAR
  // -----------------------------------------------
  @Override
  public UsuarioResponse actualizar(Long id, UsuarioRequest request, Long empresaId) {
    Usuario usuario = findByIdAndEmpresa(id, empresaId);

    // Validar rol válido
    Usuario.Rol rol;
    try {
      rol = Usuario.Rol.valueOf(request.getRol().toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Rol inválido. Use: ADMIN o CAJERO");
    }

    usuario.setNombre(request.getNombre());
    usuario.setRol(rol);

    // Si mandan username distinto, validar que no exista
    if (!usuario.getUsername().equals(request.getUsername())) {
      if (usuarioRepository.existsByUsername(request.getUsername())) {
        throw new RuntimeException("El username '" + request.getUsername() + "' ya está en uso");
      }
      usuario.setUsername(request.getUsername().toLowerCase().trim());
    }

    return toResponse(usuarioRepository.save(usuario));
  }

  // -----------------------------------------------
  // CAMBIAR ESTADO (activar / desactivar)
  // -----------------------------------------------
  @Override
  public void cambiarEstado(Long id, Boolean activo, Long empresaId) {
    Usuario usuario = findByIdAndEmpresa(id, empresaId);
    usuario.setActivo(activo);
    usuarioRepository.save(usuario);
  }

  // -----------------------------------------------
  // CAMBIAR CONTRASEÑA
  // -----------------------------------------------
  @Override
  public void cambiarPassword(Long id, CambiarPasswordRequest request, Long empresaId) {
    Usuario usuario = findByIdAndEmpresa(id, empresaId);

    // Verificar que la contraseña actual es correcta
    if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
      throw new RuntimeException("La contraseña actual es incorrecta");
    }

    // Verificar que nueva contraseña y confirmación coinciden
    if (!request.getPasswordNueva().equals(request.getPasswordNuevaConfirmacion())) {
      throw new RuntimeException("La nueva contraseña y la confirmación no coinciden");
    }

    // Validar que la nueva contraseña sea diferente a la actual
    if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
      throw new RuntimeException("La nueva contraseña debe ser diferente a la actual");
    }

    // Validar longitud mínima
    if (request.getPasswordNueva().length() < 6) {
      throw new RuntimeException("La contraseña debe tener al menos 6 caracteres");
    }

    usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
    usuarioRepository.save(usuario);
  }

  // -----------------------------------------------
  // HELPERS PRIVADOS
  // -----------------------------------------------
  private Usuario findByIdAndEmpresa(Long id, Long empresaId) {
    return usuarioRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
  }

  private UsuarioResponse toResponse(Usuario u) {
    UsuarioResponse res = new UsuarioResponse();
    res.setId(u.getId());
    res.setNombre(u.getNombre());
    res.setUsername(u.getUsername());
    res.setRol(u.getRol().name());
    res.setActivo(u.getActivo());
    res.setEmpresaId(u.getEmpresaId());
    return res;
  }
}