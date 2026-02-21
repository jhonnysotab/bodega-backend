package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "productos")
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(name = "codigo_barras", length = 100)
  private String codigoBarras;

  @Column(name = "precio_compra", precision = 10, scale = 2)
  private BigDecimal precioCompra;

  @Column(name = "precio_venta", nullable = false, precision = 10, scale = 2)
  private BigDecimal precioVenta;

  private Integer stock = 0;

  @Column(name = "stock_minimo")
  private Integer stockMinimo = 0;

  @Column(name = "categoria_id")
  private Long categoriaId;

  private Boolean activo = true;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion = LocalDateTime.now();
}