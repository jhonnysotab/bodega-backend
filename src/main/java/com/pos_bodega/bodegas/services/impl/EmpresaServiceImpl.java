package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.exception.ResourceNotFoundException;

import com.pos_bodega.bodegas.controller.dto.EmpresaRequest;
import com.pos_bodega.bodegas.controller.dto.EmpresaResponse;
import com.pos_bodega.bodegas.repository.entity.Empresa;
import com.pos_bodega.bodegas.repository.EmpresaRepository;
import com.pos_bodega.bodegas.services.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {

  private final EmpresaRepository empresaRepository;

  // -----------------------------------------------
  // CREAR
  // -----------------------------------------------
  @Override
  public EmpresaResponse crear(EmpresaRequest request) {

    // Validar RUC único si viene con valor
    if (request.getRuc() != null && !request.getRuc().isBlank()) {
      if (empresaRepository.existsByRuc(request.getRuc())) {
        throw new RuntimeException("Ya existe una empresa registrada con el RUC: " + request.getRuc());
      }
    }

    // Validar fechas
    if (request.getFechaInicio() != null && request.getFechaVencimiento() != null) {
      if (request.getFechaVencimiento().isBefore(request.getFechaInicio())) {
        throw new RuntimeException("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
      }
    }

    Empresa empresa = new Empresa();
    mapRequestToEntity(request, empresa);
    empresa.setActivo(true);

    return toResponse(empresaRepository.save(empresa));
  }

  // -----------------------------------------------
  // LISTAR TODAS
  // -----------------------------------------------
  @Override
  public List<EmpresaResponse> listar() {
    return empresaRepository.findAll()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // -----------------------------------------------
  // LISTAR SOLO ACTIVAS
  // -----------------------------------------------
  @Override
  public List<EmpresaResponse> listarActivas() {
    return empresaRepository.findByActivoTrue()
        .stream()
        .map(this::toResponse)
        .collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public EmpresaResponse obtener(Long id) {
    Empresa empresa = findById(id);
    return toResponse(empresa);
  }

  // -----------------------------------------------
  // ACTUALIZAR
  // -----------------------------------------------
  @Override
  public EmpresaResponse actualizar(Long id, EmpresaRequest request) {
    Empresa empresa = findById(id);

    // Validar RUC único si cambió
    if (request.getRuc() != null && !request.getRuc().equals(empresa.getRuc())) {
      if (empresaRepository.existsByRuc(request.getRuc())) {
        throw new RuntimeException("Ya existe una empresa registrada con el RUC: " + request.getRuc());
      }
    }

    // Validar fechas
    if (request.getFechaInicio() != null && request.getFechaVencimiento() != null) {
      if (request.getFechaVencimiento().isBefore(request.getFechaInicio())) {
        throw new RuntimeException("La fecha de vencimiento no puede ser anterior a la fecha de inicio");
      }
    }

    mapRequestToEntity(request, empresa);
    return toResponse(empresaRepository.save(empresa));
  }

  // -----------------------------------------------
  // CAMBIAR ESTADO (activar / desactivar)
  // -----------------------------------------------
  @Override
  public void cambiarEstado(Long id, Boolean activo) {
    Empresa empresa = findById(id);
    empresa.setActivo(activo);
    empresaRepository.save(empresa);
  }

  // -----------------------------------------------
  // HELPERS PRIVADOS
  // -----------------------------------------------
  private Empresa findById(Long id) {
    return empresaRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id: " + id));
  }

  private void mapRequestToEntity(EmpresaRequest request, Empresa empresa) {
    empresa.setNombre(request.getNombre());
    empresa.setRuc(request.getRuc());
    empresa.setTelefono(request.getTelefono());
    empresa.setDireccion(request.getDireccion());
    empresa.setPlan(request.getPlan());
    empresa.setFechaInicio(request.getFechaInicio());
    empresa.setFechaVencimiento(request.getFechaVencimiento());
  }

  private EmpresaResponse toResponse(Empresa e) {
    EmpresaResponse res = new EmpresaResponse();
    res.setId(e.getId());
    res.setNombre(e.getNombre());
    res.setRuc(e.getRuc());
    res.setTelefono(e.getTelefono());
    res.setDireccion(e.getDireccion());
    res.setPlan(e.getPlan());
    res.setFechaInicio(e.getFechaInicio());
    res.setFechaVencimiento(e.getFechaVencimiento());
    res.setActivo(e.getActivo());
    res.setFechaCreacion(e.getFechaCreacion());

    // Calcular estado del plan automáticamente
    if (e.getFechaVencimiento() != null) {
      res.setEstadoPlan(
          e.getFechaVencimiento().isBefore(LocalDate.now()) ? "VENCIDO" : "VIGENTE"
      );
    } else {
      res.setEstadoPlan("SIN PLAN");
    }

    return res;
  }
}