package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "gastos")
public class Gasto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(name = "caja_id")
  private Long cajaId;

  @Column(name = "categoria_id")
  private Long categoriaId;

  @Column(name = "proveedor_id")
  private Long proveedorId;

  @Column(nullable = false, length = 255)
  private String descripcion;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal monto;

  @Column(nullable = false)
  private LocalDate fecha;

  @Column(length = 255)
  private String nota;

  @Column(name = "usuario_id", nullable = false)
  private Long usuarioId;
}