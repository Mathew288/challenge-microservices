export interface ApiResponse<T> {
  status: number;
  message: string;
  correlationId?: string;
  version?: string;
  data: T;
}
