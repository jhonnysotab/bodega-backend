package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.controller.dto.ProveedorRequest;
import com.pos_bodega.bodegas.controller.dto.ProveedorResponse;
import com.pos_bodega.bodegas.repository.ProveedorCategoriaRepository;
import com.pos_bodega.bodegas.repository.entity.*;
import com.pos_bodega.bodegas.repository.entity.CategoriaRepository;
import com.pos_bodega.bodegas.repository.entity.ProveedorRepository;
import com.pos_bodega.bodegas.services.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

  private final ProveedorRepository proveedorRepository;
  private final ProveedorCategoriaRepository proveedorCategoriaRepository;
  private final CategoriaRepository categoriaRepository;

  // -----------------------------------------------
  // CREAR
  // -----------------------------------------------
  @Override
  @Transactional
  public ProveedorResponse crear(ProveedorRequest request, Long empresaId) {

    if (proveedorRepository.existsByNombreAndEmpresaId(request.getNombre(), empresaId)) {
      throw new RuntimeException("Ya existe un proveedor con el nombre: " + request.getNombre());
    }

    // Guardar proveedor
    Proveedor proveedor = new Proveedor();
    proveedor.setEmpresaId(empresaId);
    proveedor.setNombre(request.getNombre().trim());
    proveedor.setEmpresaProveedor(request.getEmpresaProveedor());
    proveedor.setTelefono(request.getTelefono());
    proveedor.setActivo(true);
    Proveedor saved = proveedorRepository.save(proveedor);

    // Asignar categorías
    asignarCategorias(saved, request.getCategoriaIds(), empresaId);

    return toResponse(proveedorRepository.findById(saved.getId()).orElseThrow());
  }

  // -----------------------------------------------
  // LISTAR TODOS
  // -----------------------------------------------
  @Override
  public List<ProveedorResponse> listar(Long empresaId) {
    return proveedorRepository.findByEmpresaId(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR ACTIVOS
  // -----------------------------------------------
  @Override
  public List<ProveedorResponse> listarActivos(Long empresaId) {
    return proveedorRepository.findByEmpresaIdAndActivoTrue(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public ProveedorResponse obtener(Long id, Long empresaId) {
    return toResponse(findByIdAndEmpresa(id, empresaId));
  }

  // -----------------------------------------------
  // ACTUALIZAR
  // -----------------------------------------------
  @Override
  @Transactional
  public ProveedorResponse actualizar(Long id, ProveedorRequest request, Long empresaId) {
    Proveedor proveedor = findByIdAndEmpresa(id, empresaId);

    // Validar nombre único si cambió
    if (!proveedor.getNombre().equalsIgnoreCase(request.getNombre())) {
      if (proveedorRepository.existsByNombreAndEmpresaId(request.getNombre(), empresaId)) {
        throw new RuntimeException("Ya existe un proveedor con el nombre: " + request.getNombre());
      }
    }

    proveedor.setNombre(request.getNombre().trim());
    proveedor.setEmpresaProveedor(request.getEmpresaProveedor());
    proveedor.setTelefono(request.getTelefono());
    proveedorRepository.save(proveedor);

    // Reasignar categorías: borra las anteriores y asigna las nuevas
    proveedorCategoriaRepository.deleteByProveedorId(proveedor.getId());
    asignarCategorias(proveedor, request.getCategoriaIds(), empresaId);

    return toResponse(proveedorRepository.findById(proveedor.getId()).orElseThrow());
  }

  // -----------------------------------------------
  // CAMBIAR ESTADO
  // -----------------------------------------------
  @Override
  public void cambiarEstado(Long id, Boolean activo, Long empresaId) {
    Proveedor proveedor = findByIdAndEmpresa(id, empresaId);
    proveedor.setActivo(activo);
    proveedorRepository.save(proveedor);
  }

  // -----------------------------------------------
  // HELPERS PRIVADOS
  // -----------------------------------------------
  private void asignarCategorias(Proveedor proveedor, List<Long> categoriaIds, Long empresaId) {
    if (categoriaIds == null || categoriaIds.isEmpty()) return;

    List<ProveedorCategoria> relaciones = new ArrayList<>();
    for (Long categoriaId : categoriaIds) {
      // Validar que la categoría pertenece a la empresa
      Categoria categoria = categoriaRepository.findByIdAndEmpresaId(categoriaId, empresaId)
          .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + categoriaId));

      ProveedorCategoria pc = new ProveedorCategoria();
      pc.setProveedor(proveedor);
      pc.setCategoria(categoria);
      relaciones.add(pc);
    }
    proveedorCategoriaRepository.saveAll(relaciones);
  }

  private Proveedor findByIdAndEmpresa(Long id, Long empresaId) {
    return proveedorRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new RuntimeException("Proveedor no encontrado con id: " + id));
  }

  private ProveedorResponse toResponse(Proveedor p) {
    ProveedorResponse res = new ProveedorResponse();
    res.setId(p.getId());
    res.setNombre(p.getNombre());
    res.setEmpresaProveedor(p.getEmpresaProveedor());
    res.setTelefono(p.getTelefono());
    res.setActivo(p.getActivo());
    res.setEmpresaId(p.getEmpresaId());
    res.setFechaCreacion(p.getFechaCreacion());

    // Mapear categorías anidadas
    if (p.getCategorias() != null) {
      List<ProveedorResponse.CategoriaInfo> cats = p.getCategorias().stream()
          .map(pc -> {
            ProveedorResponse.CategoriaInfo info = new ProveedorResponse.CategoriaInfo();
            info.setId(pc.getCategoria().getId());
            info.setNombre(pc.getCategoria().getNombre());
            return info;
          })
          .collect(Collectors.toList());
      res.setCategorias(cats);
    }

    return res;
  }
}