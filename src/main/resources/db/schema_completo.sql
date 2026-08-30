-- Esquema completo para un despliegue nuevo (VM vacía).
-- spring.jpa.hibernate.ddl-auto=validate: Hibernate NUNCA crea/altera tablas solo,
-- así que este script (o uno equivalente) debe correr ANTES de levantar el backend.
--
-- Ya incluye los cambios de las migraciones incrementales (caja_id, categoria_id/proveedor_id/nota
-- en gastos) directamente en la definición final — si ya tienes una base de datos existente y solo
-- necesitas ponerte al día, usa migration_caja.sql y migration_gastos_categoria_proveedor.sql en su
-- lugar (en ese orden).

CREATE DATABASE IF NOT EXISTS pos_bodegas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pos_bodegas;

CREATE TABLE empresas (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  nombre             VARCHAR(150) NOT NULL,
  ruc                VARCHAR(20),
  telefono           VARCHAR(20),
  direccion          VARCHAR(255),
  plan               VARCHAR(50),
  fecha_inicio       DATE,
  fecha_vencimiento  DATE,
  activo             BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id      BIGINT NOT NULL,
  nombre          VARCHAR(255),
  username        VARCHAR(255) NOT NULL UNIQUE,
  password        VARCHAR(255),
  rol             VARCHAR(20),
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_usuarios_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE categorias (
  id           BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id   BIGINT NOT NULL,
  nombre       VARCHAR(100),
  descripcion  VARCHAR(255),
  activo       BOOLEAN NOT NULL DEFAULT TRUE,
  CONSTRAINT fk_categorias_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE proveedores (
  id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id         BIGINT NOT NULL,
  nombre             VARCHAR(150) NOT NULL,
  empresa_proveedor  VARCHAR(150),
  telefono           VARCHAR(20),
  activo             BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_proveedores_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

CREATE TABLE proveedor_categorias (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  proveedor_id  BIGINT NOT NULL,
  categoria_id  BIGINT NOT NULL,
  CONSTRAINT fk_provcat_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
  CONSTRAINT fk_provcat_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE cajas (
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

CREATE TABLE productos (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id      BIGINT NOT NULL,
  nombre          VARCHAR(150) NOT NULL,
  codigo_barras   VARCHAR(100),
  precio_compra   DECIMAL(10,2),
  precio_venta    DECIMAL(10,2) NOT NULL,
  stock           INT NOT NULL DEFAULT 0,
  stock_minimo    INT DEFAULT 0,
  categoria_id    BIGINT,
  activo          BOOLEAN NOT NULL DEFAULT TRUE,
  fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_productos_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
  CONSTRAINT fk_productos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

CREATE TABLE ventas (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id      BIGINT NOT NULL,
  caja_id         BIGINT,
  numero_venta    VARCHAR(255) UNIQUE,
  total           DECIMAL(10,2) NOT NULL,
  tipo_pago       VARCHAR(20) NOT NULL,
  monto_recibido  DECIMAL(10,2),
  vuelto          DECIMAL(10,2),
  nota            VARCHAR(255),
  usuario_id      BIGINT NOT NULL,
  fecha_venta     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_ventas_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
  CONSTRAINT fk_ventas_caja FOREIGN KEY (caja_id) REFERENCES cajas(id),
  CONSTRAINT fk_ventas_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE venta_detalle (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  venta_id         BIGINT NOT NULL,
  producto_id      BIGINT NOT NULL,
  cantidad         INT NOT NULL,
  precio_unitario  DECIMAL(10,2) NOT NULL,
  subtotal         DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_ventadet_venta FOREIGN KEY (venta_id) REFERENCES ventas(id),
  CONSTRAINT fk_ventadet_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
);

CREATE TABLE gastos (
  id            BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id    BIGINT NOT NULL,
  caja_id       BIGINT,
  categoria_id  BIGINT,
  proveedor_id  BIGINT,
  descripcion   VARCHAR(255) NOT NULL,
  monto         DECIMAL(10,2) NOT NULL,
  fecha         DATE NOT NULL,
  nota          VARCHAR(255),
  usuario_id    BIGINT NOT NULL,
  CONSTRAINT fk_gastos_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
  CONSTRAINT fk_gastos_caja FOREIGN KEY (caja_id) REFERENCES cajas(id),
  CONSTRAINT fk_gastos_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
  CONSTRAINT fk_gastos_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
  CONSTRAINT fk_gastos_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

CREATE TABLE movimientos_stock (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  empresa_id       BIGINT NOT NULL,
  producto_id      BIGINT NOT NULL,
  proveedor_id     BIGINT,
  tipo_movimiento  VARCHAR(20) NOT NULL,
  cantidad         INT NOT NULL,
  stock_anterior   INT NOT NULL,
  stock_nuevo      INT NOT NULL,
  nota             VARCHAR(255),
  usuario_id       BIGINT NOT NULL,
  fecha            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_movstock_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id),
  CONSTRAINT fk_movstock_producto FOREIGN KEY (producto_id) REFERENCES productos(id),
  CONSTRAINT fk_movstock_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
  CONSTRAINT fk_movstock_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);
