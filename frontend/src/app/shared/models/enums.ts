/**
 * Enums/constantes del dominio para mantener homogeneidad con el backend.
 *
 * Nota: En ms-clientes el backend usa `String genero` sin enum. Para homogeneidad con el challenge,
 * usamos valores "Masculino" y "Femenino" (y opcional "Otro").
 *
 * Si el backend valida otra convención, centralizar el cambio aquí y en los selects.
 */
export const GENERO_VALUES = ['Masculino', 'Femenino', 'Otro'] as const;
export type Genero = (typeof GENERO_VALUES)[number];

export const TIPO_CUENTA_VALUES = ['AHORRO', 'CORRIENTE'] as const;
export type TipoCuenta = (typeof TIPO_CUENTA_VALUES)[number];

export const TIPO_MOVIMIENTO_VALUES = ['CREDITO', 'DEBITO'] as const;
export type TipoMovimiento = (typeof TIPO_MOVIMIENTO_VALUES)[number];

export const ESTADO_MOVIMIENTO_VALUES = ['PENDING', 'APPLIED', 'REJECTED'] as const;
export type EstadoMovimiento = (typeof ESTADO_MOVIMIENTO_VALUES)[number];
