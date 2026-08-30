package com.pos_bodega.bodegas.services;

import com.pos_bodega.bodegas.controller.dto.AuthResponse;
import com.pos_bodega.bodegas.controller.dto.LoginRequest;
import com.pos_bodega.bodegas.exception.AuthenticationFailedException;
import com.pos_bodega.bodegas.repository.entity.Empresa;
import com.pos_bodega.bodegas.repository.EmpresaRepository;
import com.pos_bodega.bodegas.repository.entity.Usuario;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UsuarioRepository usuarioRepository;
  private final EmpresaRepository empresaRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  public AuthResponse login(LoginRequest request) {

    // 1. Buscar usuario por username
    Usuario usuario = usuarioRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new AuthenticationFailedException("Usuario o contraseña incorrectos"));

    // 2. Verificar que el usuario esté activo
    if (Boolean.FALSE.equals(usuario.getActivo())) {
      throw new AuthenticationFailedException("Usuario inactivo, contacta al administrador");
    }

    // 3. Verificar contraseña
    if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
      throw new AuthenticationFailedException("Usuario o contraseña incorrectos");
    }

    // 4. Generar JWT con datos del usuario
    String token = jwtUtil.generateToken(
        usuario.getUsername(),
        usuario.getEmpresaId(),
        usuario.getId(),
        usuario.getRol().name()
    );

    // 5. Retornar respuesta con token y datos básicos
    String nombreEmpresa = empresaRepository.findById(usuario.getEmpresaId())
        .map(Empresa::getNombre)
        .orElse(null);

    return new AuthResponse(
        token,
        usuario.getUsername(),
        usuario.getNombre(),
        usuario.getRol().name(),
        usuario.getEmpresaId(),
        nombreEmpresa
    );
  }
}