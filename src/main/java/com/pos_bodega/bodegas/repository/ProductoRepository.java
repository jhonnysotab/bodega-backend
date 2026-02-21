package com.pos_bodega.bodegas.repository.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

  // ⚠️ MULTI-TENANT: siempre filtrar por empresa
  List<Producto> findByEmpresaId(Long empresaId);

  List<Producto> findByEmpresaIdAndActivoTrue(Long empresaId);

  Optional<Producto> findByIdAndEmpresaId(Long id, Long empresaId);

  // Buscar por código de barras
  Optional<Producto> findByCodigoBarrasAndEmpresaId(String codigoBarras, Long empresaId);

  // Validar código de barras único por empresa
  boolean existsByCodigoBarrasAndEmpresaId(String codigoBarras, Long empresaId);

  // Buscar por categoría
  List<Producto> findByCategoriaIdAndEmpresaId(Long categoriaId, Long empresaId);

  // Productos con stock bajo (stock <= stockMinimo)
  @Query("SELECT p FROM Producto p WHERE p.empresaId = :empresaId AND p.stock <= p.stockMinimo AND p.activo = true")
  List<Producto> findProductosStockBajo(@Param("empresaId") Long empresaId);

  // Buscar por nombre (contiene, ignora mayúsculas)
  List<Producto> findByEmpresaIdAndNombreContainingIgnoreCaseAndActivoTrue(Long empresaId, String nombre);
}