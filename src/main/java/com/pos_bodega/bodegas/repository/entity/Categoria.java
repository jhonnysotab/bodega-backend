package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  @Column(length = 100)
  private String nombre;

  @Column(length = 255)
  private String descripcion;

  private Boolean activo = true;
}