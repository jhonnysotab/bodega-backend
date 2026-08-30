package com.pos_bodega.bodegas.services.impl;

import com.pos_bodega.bodegas.controller.dto.CajaAperturaRequest;
import com.pos_bodega.bodegas.controller.dto.CajaCierreRequest;
import com.pos_bodega.bodegas.controller.dto.CajaResponse;
import com.pos_bodega.bodegas.exception.BusinessException;
import com.pos_bodega.bodegas.exception.ResourceNotFoundException;
import com.pos_bodega.bodegas.repository.CajaRepository;
import com.pos_bodega.bodegas.repository.GastoRepository;
import com.pos_bodega.bodegas.repository.UsuarioRepository;
import com.pos_bodega.bodegas.repository.VentaRepository;
import com.pos_bodega.bodegas.repository.entity.Caja;
import com.pos_bodega.bodegas.repository.entity.Gasto;
import com.pos_bodega.bodegas.repository.entity.Venta;
import com.pos_bodega.bodegas.services.CajaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

  private final CajaRepository cajaRepository;
  private final VentaRepository ventaRepository;
  private final GastoRepository gastoRepository;
  private final UsuarioRepository usuarioRepository;

  // -----------------------------------------------
  // ABRIR CAJA
  // -----------------------------------------------
  @Override
  @Transactional
  public CajaResponse abrir(CajaAperturaRequest request, Long empresaId, Long usuarioId) {

    cajaRepository.findByEmpresaIdAndEstado(empresaId, Caja.Estado.ABIERTA)
        .ifPresent(c -> {
          throw new BusinessException("Ya hay una caja abierta. Ciérrala antes de abrir una nueva.");
        });

    if (request.getMontoInicial() == null || request.getMontoInicial().compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessException("El monto inicial no puede ser negativo");
    }

    Caja caja = new Caja();
    caja.setEmpresaId(empresaId);
    caja.setEstado(Caja.Estado.ABIERTA);
    caja.setMontoInicial(request.getMontoInicial());
    caja.setNotaApertura(request.getNota());
    caja.setUsuarioAperturaId(usuarioId);
    caja.setFechaApertura(LocalDateTime.now());

    return toResponse(cajaRepository.save(caja));
  }

  // -----------------------------------------------
  // CERRAR CAJA (la que esté abierta actualmente)
  // -----------------------------------------------
  @Override
  @Transactional
  public CajaResponse cerrar(CajaCierreRequest request, Long empresaId, Long usuarioId) {

    Caja caja = cajaRepository.findByEmpresaIdAndEstado(empresaId, Caja.Estado.ABIERTA)
        .orElseThrow(() -> new BusinessException("No hay una caja abierta para cerrar"));

    if (request.getMontoFinalDeclarado() == null || request.getMontoFinalDeclarado().compareTo(BigDecimal.ZERO) < 0) {
      throw new BusinessException("El monto final contado no puede ser negativo");
    }

    Resumen resumen = calcularResumen(caja);
    BigDecimal efectivoEsperado = caja.getMontoInicial()
        .add(resumen.totalVentasEfectivo)
        .subtract(resumen.totalGastos);

    caja.setEstado(Caja.Estado.CERRADA);
    caja.setMontoFinalDeclarado(request.getMontoFinalDeclarado());
    caja.setMontoFinalSistema(efectivoEsperado);
    caja.setDiferencia(request.getMontoFinalDeclarado().subtract(efectivoEsperado));
    caja.setNotaCierre(request.getNota());
    caja.setUsuarioCierreId(usuarioId);
    caja.setFechaCierre(LocalDateTime.now());

    Caja guardada = cajaRepository.save(caja);
    return toResponse(guardada, resumen);
  }

  // -----------------------------------------------
  // CAJA ACTUAL (abierta)
  // -----------------------------------------------
  @Override
  public CajaResponse obtenerActual(Long empresaId) {
    return cajaRepository.findByEmpresaIdAndEstado(empresaId, Caja.Estado.ABIERTA)
        .map(this::toResponse)
        .orElse(null);
  }

  // -----------------------------------------------
  // HISTORIAL
  // -----------------------------------------------
  @Override
  public List<CajaResponse> listar(Long empresaId) {
    return cajaRepository.findByEmpresaIdOrderByFechaAperturaDesc(empresaId)
        .stream().map(this::toResponse).collect(Collectors.toList());
  }

  // -----------------------------------------------
  // OBTENER POR ID
  // -----------------------------------------------
  @Override
  public CajaResponse obtener(Long id, Long empresaId) {
    Caja caja = cajaRepository.findByIdAndEmpresaId(id, empresaId)
        .orElseThrow(() -> new ResourceNotFoundException("Caja no encontrada con id: " + id));
    return toResponse(caja);
  }

  // -----------------------------------------------
  // HELPERS
  // -----------------------------------------------
  private static class Resumen {
    int cantidadVentas;
    BigDecimal totalVentas = BigDecimal.ZERO;
    BigDecimal totalVentasEfectivo = BigDecimal.ZERO;
    BigDecimal totalVentasYape = BigDecimal.ZERO;
    BigDecimal totalVentasPlin = BigDecimal.ZERO;
    BigDecimal totalVentasTransferencia = BigDecimal.ZERO;
    BigDecimal totalGastos = BigDecimal.ZERO;
  }

  private Resumen calcularResumen(Caja caja) {
    Resumen r = new Resumen();

    List<Venta> ventas = ventaRepository.findByEmpresaIdAndCajaId(caja.getEmpresaId(), caja.getId());
    r.cantidadVentas = ventas.size();
    for (Venta v : ventas) {
      r.totalVentas = r.totalVentas.add(v.getTotal());
      switch (v.getTipoPago()) {
        case EFECTIVO -> r.totalVentasEfectivo = r.totalVentasEfectivo.add(v.getTotal());
        case YAPE -> r.totalVentasYape = r.totalVentasYape.add(v.getTotal());
        case PLIN -> r.totalVentasPlin = r.totalVentasPlin.add(v.getTotal());
        case TRANSFERENCIA -> r.totalVentasTransferencia = r.totalVentasTransferencia.add(v.getTotal());
      }
    }

    List<Gasto> gastos = gastoRepository.findByEmpresaIdAndCajaId(caja.getEmpresaId(), caja.getId());
    for (Gasto g : gastos) {
      r.totalGastos = r.totalGastos.add(g.getMonto());
    }

    return r;
  }

  private CajaResponse toResponse(Caja caja) {
    return toResponse(caja, calcularResumen(caja));
  }

  private CajaResponse toResponse(Caja caja, Resumen resumen) {
    CajaResponse res = new CajaResponse();
    res.setId(caja.getId());
    res.setEstado(caja.getEstado().name());
    res.setMontoInicial(caja.getMontoInicial());
    res.setMontoFinalDeclarado(caja.getMontoFinalDeclarado());
    res.setMontoFinalSistema(caja.getMontoFinalSistema());
    res.setDiferencia(caja.getDiferencia());
    res.setNotaApertura(caja.getNotaApertura());
    res.setNotaCierre(caja.getNotaCierre());
    res.setUsuarioAperturaId(caja.getUsuarioAperturaId());
    res.setUsuarioCierreId(caja.getUsuarioCierreId());
    res.setFechaApertura(caja.getFechaApertura());
    res.setFechaCierre(caja.getFechaCierre());

    usuarioRepository.findById(caja.getUsuarioAperturaId())
        .ifPresent(u -> res.setUsuarioAperturaNombre(u.getNombre()));
    if (caja.getUsuarioCierreId() != null) {
      usuarioRepository.findById(caja.getUsuarioCierreId())
          .ifPresent(u -> res.setUsuarioCierreNombre(u.getNombre()));
    }

    res.setCantidadVentas(resumen.cantidadVentas);
    res.setTotalVentas(resumen.totalVentas);
    res.setTotalVentasEfectivo(resumen.totalVentasEfectivo);
    res.setTotalVentasYape(resumen.totalVentasYape);
    res.setTotalVentasPlin(resumen.totalVentasPlin);
    res.setTotalVentasTransferencia(resumen.totalVentasTransferencia);
    res.setTotalGastos(resumen.totalGastos);
    res.setEfectivoEsperado(
        caja.getMontoInicial().add(resumen.totalVentasEfectivo).subtract(resumen.totalGastos));

    return res;
  }
}
