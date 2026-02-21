package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.MovimientoStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoStockRepository extends JpaRepository<MovimientoStock, Long> {

  // ⚠️ MULTI-TENANT
  List<MovimientoStock> findByEmpresaIdOrderByFechaDesc(Long empresaId);

  // Movimientos de un producto específico
  List<MovimientoStock> findByProductoIdAndEmpresaIdOrderByFechaDesc(Long productoId, Long empresaId);

  // Movimientos por proveedor
  List<MovimientoStock> findByProveedorIdAndEmpresaIdOrderByFechaDesc(Long proveedorId, Long empresaId);
}
