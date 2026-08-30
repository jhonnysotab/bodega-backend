package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cajas")
public class Caja {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Estado estado = Estado.ABIERTA;

  @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
  private BigDecimal montoInicial;

  @Column(name = "monto_final_declarado", precision = 10, scale = 2)
  private BigDecimal montoFinalDeclarado;

  @Column(name = "monto_final_sistema", precision = 10, scale = 2)
  private BigDecimal montoFinalSistema;

  @Column(precision = 10, scale = 2)
  private BigDecimal diferencia;

  @Column(name = "nota_apertura", length = 255)
  private String notaApertura;

  @Column(name = "nota_cierre", length = 255)
  private String notaCierre;

  @Column(name = "usuario_apertura_id", nullable = false)
  private Long usuarioAperturaId;

  @Column(name = "usuario_cierre_id")
  private Long usuarioCierreId;

  @Column(name = "fecha_apertura", nullable = false)
  private LocalDateTime fechaApertura = LocalDateTime.now();

  @Column(name = "fecha_cierre")
  private LocalDateTime fechaCierre;

  public enum Estado {
    ABIERTA, CERRADA
  }
}
