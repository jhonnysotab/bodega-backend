package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.ProveedorCategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorCategoriaRepository extends JpaRepository<ProveedorCategoria, Long> {

  // Eliminar todas las categorías de un proveedor (para reasignar)
  void deleteByProveedorId(Long proveedorId);
}