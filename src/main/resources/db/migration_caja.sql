-- Migración manual: apertura/cierre de caja
-- Ejecutar contra la base de datos pos_bodegas (spring.jpa.hibernate.ddl-auto=validate,
-- así que Hibernate no crea/altera tablas solo; hay que aplicar esto a mano).

CREATE TABLE IF NOT EXISTS cajas (
  id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id             BIGINT NOT NULL,
  estado                 VARCHAR(20) NOT NULL,
  monto_inicial          DECIMAL(10,2) NOT NULL,
  monto_final_declarado  DECIMAL(10,2),
  monto_final_sistema    DECIMAL(10,2),
  diferencia             DECIMAL(10,2),
  nota_apertura          VARCHAR(255),
  nota_cierre            VARCHAR(255),
  usuario_apertura_id    BIGINT NOT NULL,
  usuario_cierre_id      BIGINT,
  fecha_apertura         DATETIME NOT NULL,
  fecha_cierre           DATETIME,
  CONSTRAINT fk_cajas_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
  CONSTRAINT fk_cajas_usuario_apertura FOREIGN KEY (usuario_apertura_id) REFERENCES usuarios(id),
  CONSTRAINT fk_cajas_usuario_cierre FOREIGN KEY (usuario_cierre_id) REFERENCES usuarios(id)
);

CREATE INDEX idx_cajas_empresa_estado ON cajas (empresa_id, estado);

ALTER TABLE ventas
  ADD COLUMN caja_id BIGINT NULL AFTER empresa_id,
  ADD CONSTRAINT fk_ventas_caja FOREIGN KEY (caja_id) REFERENCES cajas(id);

ALTER TABLE gastos
  ADD COLUMN caja_id BIGINT NULL AFTER empresa_id,
  ADD CONSTRAINT fk_gastos_caja FOREIGN KEY (caja_id) REFERENCES cajas(id);
