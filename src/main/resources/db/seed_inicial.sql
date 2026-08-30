-- Datos mínimos para poder iniciar sesión por primera vez.
-- No hay endpoint de registro (solo /api/auth/login), así que la primera empresa y el primer
-- usuario ADMIN se crean a mano con este script, después de correr schema_completo.sql.
--
-- Usuario:   admin
-- Password:  bodega123   (¡cámbiala apenas entres! usuarios -> editar -> cambiar contraseña,
--                          o UPDATE usuarios SET password = ... con un hash bcrypt nuevo)

USE pos_bodegas;

INSERT INTO empresas (nombre, ruc, activo)
VALUES ('Mi Bodega', NULL, TRUE);

-- La contraseña de abajo es el hash bcrypt de "bodega123".
INSERT INTO usuarios (empresa_id, nombre, username, password, rol, activo)
VALUES (
  (SELECT id FROM empresas ORDER BY id DESC LIMIT 1),
  'Administrador',
  'admin',
  '$2b$10$P9U/XCUvyOVX5SQ4zXr1cea.l6u.U4EtxmTmKgDaMEsfMjR5dnLAK',
  'ADMIN',
  TRUE
);
