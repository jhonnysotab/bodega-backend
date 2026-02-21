package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {
}