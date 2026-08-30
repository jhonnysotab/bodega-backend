package com.pos_bodega.bodegas.repository;

import com.pos_bodega.bodegas.repository.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  Optional<Usuario> findByUsername(String username);

  boolean existsByUsername(String username);

  // ⚠️ MULTI-TENANT: listar solo usuarios de la empresa
  List<Usuario> findByEmpresaId(Long empresaId);

  // Buscar por id Y empresa (seguridad multi-tenant)
  Optional<Usuario> findByIdAndEmpresaId(Long id, Long empresaId);

  // Validar username único al crear/actualizar
}