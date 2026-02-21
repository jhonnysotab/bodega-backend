package com.pos_bodega.bodegas.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

  // ⚠️ MULTI-TENANT: siempre filtrar por empresa
  List<Categoria> findByEmpresaId(Long empresaId);

  List<Categoria> findByEmpresaIdAndActivoTrue(Long empresaId);

  Optional<Categoria> findByIdAndEmpresaId(Long id, Long empresaId);

  // Validar nombre único por empresa
  boolean existsByNombreAndEmpresaId(String nombre, Long empresaId);
}