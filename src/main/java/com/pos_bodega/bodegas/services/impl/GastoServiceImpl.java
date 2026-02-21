package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.controller.dto.GastoRequest;
import com.pos_bodega.bodegas.controller.dto.GastoResponse;
import com.pos_bodega.bodegas.repository.GastoRepository;
import com.pos_bodega.bodegas.repository.entity.Gasto;
import com.pos_bodega.bodegas.repository.entity.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl {

  private final GastoRepository gastoRepository;
  private final UsuarioRepository usuarioRepository;

  // -----------------------------------------------
  // REGISTRAR GASTO
  // -----------------------------------------------
  public GastoResponse crear(GastoRequest request, Long empresaId, Long usuarioId) {

    if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("El monto debe ser mayor a 0");
    }

    Gasto gasto = new Gasto();
    gasto.setEmpresaId(empresaId);
    gasto.setDescripcion(request.getDescripcion().trim());
    gasto.setMonto(request.getMonto());
    gasto.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
    gasto.setUsuarioId(usuarioId);

    return toResponse(gastoRepository.save(gasto));
  }

  // -----------------------------------------------
  // LISTAR TODOS
  // -----------------------------------------------
  public List<GastoResponse> listar(Long empresaId) {
    return gastoRepository.findByEmpresaIdOrderByFechaDesc(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR RANGO DE FECHA
  // -----------------------------------------------
  public List<GastoResponse> listarPorFecha(Long empresaId, LocalDate desde, LocalDate hasta) {
    return gastoRepository.findByEmpresaIdAndFechaBetweenOrderByFechaDesc(empresaId, desde, hasta)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER
  // -----------------------------------------------
  public GastoResponse obtener(Long id, Long empresaId) {
    Gasto gasto = gastoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new RuntimeException("Gasto no encontrado con id: " + id));
    return toResponse(gasto);
  }

  // -----------------------------------------------
  // ELIMINAR
  // -----------------------------------------------
  public void eliminar(Long id, Long empresaId) {
    Gasto gasto = gastoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new RuntimeException("Gasto no encontrado con id: " + id));
    gastoRepository.delete(gasto);
  }

  // -----------------------------------------------
  // HELPER
  // -----------------------------------------------
  private GastoResponse toResponse(Gasto g) {
    GastoResponse res = new GastoResponse();
    res.setId(g.getId());
    res.setDescripcion(g.getDescripcion());
    res.setMonto(g.getMonto());
    res.setFecha(g.getFecha());
    res.setUsuarioId(g.getUsuarioId());
    usuarioRepository.findById(g.getUsuarioId())
        .ifPresent(u -> res.setUsuarioNombre(u.getNombre()));
    return res;
  }
}