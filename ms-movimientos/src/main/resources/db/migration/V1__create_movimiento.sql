-- ms-movimientos | Flyway V1
-- Movimiento (bounded context: movimientos)
-- No FK cross-service: cuenta_id es referencia externa a ms-cuentas.

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS movimiento (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  cuenta_id UUID NOT NULL,
  fecha TIMESTAMP NOT NULL,

  tipo VARCHAR(20) NOT NULL, -- CREDITO | DEBITO
  valor NUMERIC(19,2) NOT NULL,

  saldo_disponible NUMERIC(19,2), -- denormalizado desde ms-cuentas
  estado VARCHAR(20) NOT NULL, -- PENDING | APPLIED | REJECTED

  saga_id UUID,
  command_id UUID,

  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS ix_movimiento_cuenta_id ON movimiento (cuenta_id);
CREATE INDEX IF NOT EXISTS ix_movimiento_fecha ON movimiento (fecha);
CREATE UNIQUE INDEX IF NOT EXISTS ux_movimiento_command_id ON movimiento (command_id);
