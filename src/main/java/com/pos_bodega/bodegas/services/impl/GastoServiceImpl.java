package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.GastoRequest;
import com.pos_bodega.bodegas.controller.dto.GastoResponse;
import com.pos_bodega.bodegas.repository.CajaRepository;
import com.pos_bodega.bodegas.repository.CategoriaRepository;
import com.pos_bodega.bodegas.repository.GastoRepository;
import com.pos_bodega.bodegas.repository.ProveedorRepository;
import com.pos_bodega.bodegas.repository.entity.Caja;
import com.pos_bodega.bodegas.repository.entity.Gasto;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.services.GastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GastoServiceImpl implements GastoService {

  private final GastoRepository gastoRepository;
  private final UsuarioRepository usuarioRepository;
  private final CajaRepository cajaRepository;
  private final CategoriaRepository categoriaRepository;
  private final ProveedorRepository proveedorRepository;

  // -----------------------------------------------
  // REGISTRAR GASTO
  // -----------------------------------------------
  @Override
  public GastoResponse crear(GastoRequest request, Long empresaId, Long usuarioId) {

    if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("El monto debe ser mayor a 0");
    }

    validarCategoriaYProveedor(request, empresaId);

    Gasto gasto = new Gasto();
    gasto.setEmpresaId(empresaId);
    gasto.setDescripcion(request.getDescripcion().trim());
    gasto.setMonto(request.getMonto());
    gasto.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
    gasto.setUsuarioId(usuarioId);
    gasto.setCategoriaId(request.getCategoriaId());
    gasto.setProveedorId(request.getProveedorId());
    gasto.setNota(request.getNota());

    // Si hay una caja abierta, el gasto se descuenta del efectivo de ese turno
    cajaRepository.findByEmpresaIdAndEstado(empresaId, Caja.Estado.ABIERTA)
        .ifPresent(caja -> gasto.setCajaId(caja.getId()));

    return toResponse(gastoRepository.save(gasto));
  }

  // -----------------------------------------------
  // ACTUALIZAR GASTO
  // -----------------------------------------------
  @Override
  public GastoResponse actualizar(Long id, GastoRequest request, Long empresaId) {

    Gasto gasto = gastoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con id: " + id));

    if (request.getMonto() == null || request.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
      throw new RuntimeException("El monto debe ser mayor a 0");
    }
    validarCategoriaYProveedor(request, empresaId);

    gasto.setDescripcion(request.getDescripcion().trim());
    gasto.setMonto(request.getMonto());
    gasto.setFecha(request.getFecha() != null ? request.getFecha() : gasto.getFecha());
    gasto.setCategoriaId(request.getCategoriaId());
    gasto.setProveedorId(request.getProveedorId());
    gasto.setNota(request.getNota());

    return toResponse(gastoRepository.save(gasto));
  }

  // -----------------------------------------------
  // LISTAR TODOS
  // -----------------------------------------------
  @Override
  public List<GastoResponse> listar(Long empresaId) {
    return gastoRepository.findByEmpresaIdOrderByFechaDesc(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR POR RANGO DE FECHA
  // -----------------------------------------------
  @Override
  public List<GastoResponse> listarPorFecha(Long empresaId, LocalDate desde, LocalDate hasta) {
    return gastoRepository.findByEmpresaIdAndFechaBetweenOrderByFechaDesc(empresaId, desde, hasta)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER
  // -----------------------------------------------
  @Override
  public GastoResponse obtener(Long id, Long empresaId) {
    Gasto gasto = gastoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con id: " + id));
    return toResponse(gasto);
  }

  // -----------------------------------------------
  // ELIMINAR
  // -----------------------------------------------
  @Override
  public void eliminar(Long id, Long empresaId) {
    Gasto gasto = gastoRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con id: " + id));
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
    res.setNota(g.getNota());
    res.setUsuarioId(g.getUsuarioId());
    usuarioRepository.findById(g.getUsuarioId())
        .ifPresent(u -> res.setUsuarioNombre(u.getNombre()));

    res.setCategoriaId(g.getCategoriaId());
    if (g.getCategoriaId() != null) {
      categoriaRepository.findById(g.getCategoriaId())
          .ifPresent(c -> res.setCategoriaNombre(c.getNombre()));
    }

    res.setProveedorId(g.getProveedorId());
    if (g.getProveedorId() != null) {
      proveedorRepository.findById(g.getProveedorId())
          .ifPresent(p -> res.setProveedorNombre(p.getNombre()));
    }

    return res;
  }

  private void validarCategoriaYProveedor(GastoRequest request, Long empresaId) {
    if (request.getCategoriaId() != null
        && categoriaRepository.findByIdAndEmpresaId(request.getCategoriaId(), empresaId).isEmpty()) {
      throw new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoriaId());
    }
    if (request.getProveedorId() != null
        && proveedorRepository.findByIdAndEmpresaId(request.getProveedorId(), empresaId).isEmpty()) {
      throw new ResourceNotFoundException("Proveedor no encontrado con id: " + request.getProveedorId());
    }
  }
}