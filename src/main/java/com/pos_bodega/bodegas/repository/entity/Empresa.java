package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "empresas")
public class Empresa {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(length = 20)
  private String ruc;

  @Column(length = 20)
  private String telefono;

  @Column(length = 255)
  private String direccion;

  @Column(length = 50)
  private String plan;

  @Column(name = "fecha_inicio")
  private LocalDate fechaInicio;

  @Column(name = "fecha_vencimiento")
  private LocalDate fechaVencimiento;

  private Boolean activo = true;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion = LocalDateTime.now();
}