package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "proveedores")
public class Proveedor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(name = "empresa_proveedor", length = 150)
  private String empresaProveedor;

  @Column(length = 20)
  private String telefono;

  private Boolean activo = true;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion = LocalDateTime.now();

  // Relación con categorías que maneja este proveedor
  @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ProveedorCategoria> categorias;
}