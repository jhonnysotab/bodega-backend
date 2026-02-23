package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.controller.dto.CategoriaRequest;
import com.pos_bodega.bodegas.controller.dto.CategoriaResponse;
import com.pos_bodega.bodegas.repository.entity.Categoria;
import com.pos_bodega.bodegas.repository.entity.CategoriaRepository;
import com.pos_bodega.bodegas.services.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoriaRepository categoriaRepository;

  // -----------------------------------------------
  // CREAR
  // -----------------------------------------------
  @Override
  public CategoriaResponse crear(CategoriaRequest request, Long empresaId ) {

    // Validar nombre único dentro de la empresa
    if (categoriaRepository.existsByNombreAndEmpresaId(request.getNombre(), request.getEmpresaId())) {
      throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
    }

    Categoria categoria = new Categoria();
    categoria.setEmpresaId(empresaId);
    categoria.setNombre(request.getNombre().trim());
    categoria.setDescripcion(request.getDescripcion());
    categoria.setActivo(true);

    return toResponse(categoriaRepository.save(categoria));
  }

  // -----------------------------------------------
  // LISTAR TODAS (activas e inactivas)
  // -----------------------------------------------
  @Override
  public List<CategoriaResponse> listar(Long empresaId) {
    return categoriaRepository.findByEmpresaId(empresaId)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR SOLO ACTIVAS
  // -----------------------------------------------
  @Override
  public List<CategoriaResponse> listarActivas(Long empresaId) {
    return categoriaRepository.findByEmpresaIdAndActivoTrue(empresaId)
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public CategoriaResponse obtener(Long id, Long empresaId) {
    return toResponse(findByIdAndEmpresa(id, empresaId));
  }

  // -----------------------------------------------
  // ACTUALIZAR
  // -----------------------------------------------
  @Override
  public CategoriaResponse actualizar(Long id, CategoriaRequest request, Long empresaId) {
    Categoria categoria = findByIdAndEmpresa(id, empresaId);

    // Validar nombre único si cambió
    if (!categoria.getNombre().equalsIgnoreCase(request.getNombre())) {
      if (categoriaRepository.existsByNombreAndEmpresaId(request.getNombre(), empresaId)) {
        throw new RuntimeException("Ya existe una categoría con el nombre: " + request.getNombre());
      }
    }

    categoria.setNombre(request.getNombre().trim());
    categoria.setDescripcion(request.getDescripcion());

    return toResponse(categoriaRepository.save(categoria));
  }

  // -----------------------------------------------
  // CAMBIAR ESTADO
  // -----------------------------------------------
  @Override
  public void cambiarEstado(Long id, Boolean activo, Long empresaId) {
    Categoria categoria = findByIdAndEmpresa(id, empresaId);
    categoria.setActivo(activo);
    categoriaRepository.save(categoria);
  }

  // -----------------------------------------------
  // HELPERS PRIVADOS
  // -----------------------------------------------
  private Categoria findByIdAndEmpresa(Long id, Long empresaId) {
    return categoriaRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new RuntimeException("Categoría no encontrada con id: " + id));
  }

  private CategoriaResponse toResponse(Categoria c) {
    CategoriaResponse res = new CategoriaResponse();
    res.setId(c.getId());
    res.setNombre(c.getNombre());
    res.setDescripcion(c.getDescripcion());
    res.setActivo(c.getActivo());
    res.setEmpresaId(c.getEmpresaId());
    return res;
  }
}