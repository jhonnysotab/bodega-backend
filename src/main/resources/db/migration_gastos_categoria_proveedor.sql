-- Migración manual: categoría y proveedor en gastos
-- Ejecutar contra la base de datos pos_bodegas (spring.jpa.hibernate.ddl-auto=validate,
-- así que Hibernate no crea/altera tablas solo; hay que aplicar esto a mano).

ALTER TABLE gastos
  ADD COLUMN categoria_id BIGINT NULL AFTER caja_id,
  ADD COLUMN proveedor_id BIGINT NULL AFTER categoria_id,
  ADD COLUMN nota VARCHAR(255) NULL AFTER fecha,
  ADD CONSTRAINT fk_gastos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  ADD CONSTRAINT fk_gastos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id);
