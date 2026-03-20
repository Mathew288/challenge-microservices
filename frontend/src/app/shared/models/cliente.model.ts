export interface Cliente {
  id: string; // UUID
  identificacion: string;
  nombre: string;
  genero: string;
  edad: number;
  direccion: string;
  telefono: string;
  estado: boolean;
  createdAt?: string; // Instant
  updatedAt?: string; // Instant
}

export interface ClienteCreateRequest {
  identificacion: string;
  nombre: string;
  genero: string;
  edad: number;
  direccion: string;
  telefono: string;
  password: string;
  estado: boolean;
}

export interface ClienteUpdateRequest {
  nombre?: string;
  genero?: string;
  edad?: number;
  direccion?: string;
  telefono?: string;
  password?: string;
  estado?: boolean;
}
