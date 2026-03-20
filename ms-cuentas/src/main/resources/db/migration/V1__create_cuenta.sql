-- ms-cuentas | Flyway V1
-- Cuenta (bounded context: cuentas)
-- No FK cross-service: cliente_id es referencia externa a ms-clientes.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS cuenta (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  numero_cuenta VARCHAR(20) NOT NULL,
  tipo VARCHAR(20) NOT NULL, -- AHORRO | CORRIENTE
  saldo_inicial NUMERIC(19,2) NOT NULL,
  saldo_actual  NUMERIC(19,2) NOT NULL,
  estado BOOLEAN NOT NULL DEFAULT TRUE,

  cliente_id UUID NOT NULL,

  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS ux_cuenta_numero_cuenta ON cuenta (numero_cuenta);
CREATE INDEX IF NOT EXISTS ix_cuenta_cliente_id ON cuenta (cliente_id);
