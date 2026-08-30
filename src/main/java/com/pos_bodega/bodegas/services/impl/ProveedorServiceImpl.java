package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.ProveedorRequest;
import com.pos_bodega.bodegas.controller.dto.ProveedorResponse;
import com.pos_bodega.bodegas.repository.ProveedorCategoriaRepository;
import com.pos_bodega.bodegas.repository.ProveedorRepository;
import com.pos_bodega.bodegas.repository.entity.*;
import com.pos_bodega.bodegas.repository.CategoriaRepository;
import com.pos_bodega.bodegas.services.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

  private final ProveedorRepository proveedorRepository;
  private final ProveedorCategoriaRepository proveedorCategoriaRepository;
  private final CategoriaRepository categoriaRepository;

  // ── CREAR ─────────────────────────────────────────────
  @Transactional
  public ProveedorResponse crear(ProveedorRequest request, Long empresaId) {
    Proveedor proveedor = new Proveedor();
    proveedor.setEmpresaId(empresaId);
    proveedor.setNombre(request.getNombre());
    proveedor.setEmpresaProveedor(request.getEmpresaProveedor());
    proveedor.setTelefono(request.getTelefono());
    Proveedor guardado = proveedorRepository.save(proveedor);

    asignarCategorias(guardado, request.getCategoriaIds(), empresaId);
    return toResponse(guardado);
  }

  // ── ACTUALIZAR ────────────────────────────────────────
  @Transactional
  public ProveedorResponse actualizar(Long id, ProveedorRequest request, Long empresaId) {
    Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));

    proveedor.setNombre(request.getNombre());
    proveedor.setEmpresaProveedor(request.getEmpresaProveedor());
    proveedor.setTelefono(request.getTelefono());
    proveedorRepository.save(proveedor);

    // ✅ CLAVE: eliminar TODAS las categorías anteriores antes de reasignar
    proveedorCategoriaRepository.deleteByProveedorId(proveedor.getId());

    // Asignar las nuevas categorías
    asignarCategorias(proveedor, request.getCategoriaIds(), empresaId);

    return toResponse(proveedor);
  }

  // ── LISTAR ────────────────────────────────────────────
  public List<ProveedorResponse> listar(Long empresaId) {
    return proveedorRepository.findByEmpresaIdOrderByNombreAsc(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // ── LISTAR ACTIVOS ────────────────────────────────────
  public List<ProveedorResponse> listarActivos(Long empresaId) {
    return proveedorRepository.findByEmpresaIdAndActivoTrue(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // ── OBTENER ───────────────────────────────────────────
  public ProveedorResponse obtener(Long id, Long empresaId) {
    Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
    return toResponse(proveedor);
  }

  // ── CAMBIAR ESTADO ────────────────────────────────────
  @Transactional
  public ProveedorResponse cambiarEstado(Long id, boolean activo, Long empresaId) {
    Proveedor proveedor = proveedorRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
    proveedor.setActivo(activo);
    return toResponse(proveedorRepository.save(proveedor));
  }

  // ── HELPERS ───────────────────────────────────────────
  private void asignarCategorias(Proveedor proveedor, List<Long> categoriaIds, Long empresaId) {
    if (categoriaIds == null || categoriaIds.isEmpty()) return;

    for (Long catId : categoriaIds) {
      // Validar que la categoría pertenece a la empresa
      categoriaRepository.findByIdAndEmpresaId(catId, empresaId)
          .ifPresent(cat -> {
            ProveedorCategoria pc = new ProveedorCategoria();
            pc.setProveedor(proveedor);
            pc.setCategoria(cat);
            proveedorCategoriaRepository.save(pc);
          });
    }
  }

  private ProveedorResponse toResponse(Proveedor p) {
    ProveedorResponse res = new ProveedorResponse();
    res.setId(p.getId());
    res.setNombre(p.getNombre());
    res.setEmpresaProveedor(p.getEmpresaProveedor());
    res.setTelefono(p.getTelefono());
    res.setActivo(p.getActivo());

    // Categorías anidadas
    List<ProveedorCategoria> pcs = proveedorCategoriaRepository.findByProveedorId(p.getId());
    List<ProveedorResponse.CategoriaInfo> cats = pcs.stream()
        .map(pc -> categoriaRepository.findById(pc.getCategoria().getId())
            .map(c -> {
              ProveedorResponse.CategoriaInfo ci = new ProveedorResponse.CategoriaInfo();
              ci.setId(c.getId());
              ci.setNombre(c.getNombre());
              return ci;
            }).orElse(null))
        .filter(ci -> ci != null)
        .collect(Collectors.toList());

    res.setCategorias(cats);
    return res;
  }
}