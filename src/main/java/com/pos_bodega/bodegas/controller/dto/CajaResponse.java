package com.pos_bodega.bodegas.controller.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CajaResponse {
  private Long id;
  private String estado;

  private BigDecimal montoInicial;
  private BigDecimal montoFinalDeclarado;
  private BigDecimal montoFinalSistema;
  private BigDecimal diferencia;

  private String notaApertura;
  private String notaCierre;

  private Long usuarioAperturaId;
  private String usuarioAperturaNombre;
  private Long usuarioCierreId;
  private String usuarioCierreNombre;

  private LocalDateTime fechaApertura;
  private LocalDateTime fechaCierre;

  // Resumen de movimientos del turno (en vivo si está ABIERTA, final si está CERRADA)
  private Integer cantidadVentas;
  private BigDecimal totalVentas;
  private BigDecimal totalVentasEfectivo;
  private BigDecimal totalVentasYape;
  private BigDecimal totalVentasPlin;
  private BigDecimal totalVentasTransferencia;
  private BigDecimal totalGastos;
  private BigDecimal efectivoEsperado;
}
