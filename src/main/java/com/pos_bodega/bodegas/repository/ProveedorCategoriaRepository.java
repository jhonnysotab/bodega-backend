package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.ProveedorCategoria;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProveedorCategoriaRepository extends JpaRepository<ProveedorCategoria, Long> {

  List<ProveedorCategoria> findByProveedorId(Long proveedorId);

  @Modifying
  @Transactional
  @Query("DELETE FROM ProveedorCategoria pc WHERE pc.proveedor.id = :proveedorId")
  void deleteByProveedorId(@Param("proveedorId") Long proveedorId);
}