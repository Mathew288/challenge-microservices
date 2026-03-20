-- ms-clientes | Flyway V1
-- JPA es el ORM (entidades + repositorios). Flyway versiona el esquema.
-- ddl-auto=validate asegura alineación entre entidad y tabla.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS cliente (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  identificacion VARCHAR(20) NOT NULL,
  nombre VARCHAR(120) NOT NULL,
  genero VARCHAR(20) NOT NULL,
  edad INTEGER NOT NULL,
  direccion VARCHAR(255) NOT NULL,
  telefono VARCHAR(30) NOT NULL,

  password_hash VARCHAR(255) NOT NULL,
  estado BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cliente_identificacion ON cliente (identificacion);
