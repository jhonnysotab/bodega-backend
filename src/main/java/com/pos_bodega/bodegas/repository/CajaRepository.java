package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {

  // ⚠️ MULTI-TENANT
  Optional<Caja> findByEmpresaIdAndEstado(Long empresaId, Caja.Estado estado);

  Optional<Caja> findByIdAndEmpresaId(Long id, Long empresaId);

  List<Caja> findByEmpresaIdOrderByFechaAperturaDesc(Long empresaId);
}
