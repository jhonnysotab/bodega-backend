package com.pos_bodega.bodegas.repository.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "empresa_id", nullable = false)
  private Long empresaId;

  private String nombre;

  @Column(unique = true, nullable = false)
  private String username;

  private String password;

  @Enumerated(EnumType.STRING)
  private Rol rol;

  private Boolean activo = true;

  @Column(name = "fecha_creacion")
  private LocalDateTime fechaCreacion = LocalDateTime.now();



  public enum Rol {
    ADMIN, CAJERO
  }
}