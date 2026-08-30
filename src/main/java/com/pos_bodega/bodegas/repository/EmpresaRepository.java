package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

  // Listar solo empresas activas
  List<Empresa> findByActivoTrue();

  // Verificar si ya existe una empresa con ese RUC
  boolean existsByRuc(String ruc);

  // Buscar por RUC (para validaciones)
  Optional<Empresa> findByRuc(String ruc);
}