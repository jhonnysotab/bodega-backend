package com.pos_bodega.bodegas.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {

  // ⚠️ MULTI-TENANT
  List<Proveedor> findByEmpresaId(Long empresaId);

  List<Proveedor> findByEmpresaIdAndActivoTrue(Long empresaId);

  Optional<Proveedor> findByIdAndEmpresaId(Long id, Long empresaId);

  boolean existsByNombreAndEmpresaId(String nombre, Long empresaId);
}