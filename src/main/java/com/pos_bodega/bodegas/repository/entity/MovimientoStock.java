package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "movimientos_stock")
public class MovimientoStock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(name = "producto_id", nullable = false)
  private Long productoId;

  @Column(name = "proveedor_id")
  private Long proveedorId;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_movimiento", nullable = false)
  private TipoMovimiento tipoMovimiento;

  @Column(nullable = false)
  private Integer cantidad;

  @Column(name = "stock_anterior", nullable = false)
  private Integer stockAnterior;

  @Column(name = "stock_nuevo", nullable = false)
  private Integer stockNuevo;

  @Column(length = 255)
  private String nota;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  private LocalDateTime fecha = LocalDateTime.now();

  public enum TipoMovimiento {
    VENTA, INGRESO, AJUSTE
  }
}