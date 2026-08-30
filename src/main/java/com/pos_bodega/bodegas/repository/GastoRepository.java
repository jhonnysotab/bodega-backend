package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface GastoRepository extends JpaRepository<Gasto, Long> {

  // ⚠️ MULTI-TENANT
  List<Gasto> findByEmpresaIdOrderByFechaDesc(Long empresaId);

  Optional<Gasto> findByIdAndEmpresaId(Long id, Long empresaId);

  // Gastos por rango de fecha
  List<Gasto> findByEmpresaIdAndFechaBetweenOrderByFechaDesc(Long empresaId, LocalDate desde, LocalDate hasta);

  // Gastos de un turno de caja (para el arqueo)
  List<Gasto> findByEmpresaIdAndCajaId(Long empresaId, Long cajaId);
}