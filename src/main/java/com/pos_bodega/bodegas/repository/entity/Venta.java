package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "ventas")
public class Venta {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(name = "numero_venta", unique = true)
  private String numeroVenta;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal total;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_pago", nullable = false)
  private TipoPago tipoPago;

  @Column(name = "monto_recibido", precision = 10, scale = 2)
  private BigDecimal montoRecibido;

  @Column(precision = 10, scale = 2)
  private BigDecimal vuelto;

  @Column(length = 255)
  private String nota;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;

  @Column(name = "fecha_venta")
  private LocalDateTime fechaVenta = LocalDateTime.now();

  @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL)
  private List<VentaDetalle> detalles;

  public enum TipoPago {
    EFECTIVO, YAPE, PLIN, TRANSFERENCIA
  }
}