package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

  List<Venta> findByEmpresaIdOrderByFechaVentaDesc(Long empresaId);

  Optional<Venta> findByIdAndEmpresaId(Long id, Long empresaId);

  // Ventas del día
  List<Venta> findByEmpresaIdAndFechaVentaBetween(Long empresaId, LocalDateTime desde, LocalDateTime hasta);

  // Último número de venta para generar el siguiente
  @Query("SELECT v.numeroVenta FROM Venta v WHERE v.empresaId = :empresaId ORDER BY v.id DESC LIMIT 1")
  Optional<String> findUltimoNumeroVenta(@Param("empresaId") Long empresaId);
}