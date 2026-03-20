import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { clientesApi } from '../../../../core/api/clientes.api';
import { reportesApi } from '../../../../core/api/reportes.api';

import { AlertComponent } from '../../../../shared/components/ui/alert/alert.component';
import { ButtonComponent } from '../../../../shared/components/ui/button/button.component';
import { InputComponent } from '../../../../shared/components/ui/input/input.component';
import { SelectComponent, SelectOption } from '../../../../shared/components/ui/select/select.component';
import { TableColumn, TableComponent } from '../../../../shared/components/ui/table/table.component';

import { Cliente } from '../../../../shared/models/cliente.model';
import {
  ReporteCuentaItem,
  ReporteEstadoCuentaResponse,
  ReporteMovimientoItem,
} from '../../../../shared/models/reporte-estado-cuenta.model';

type ReporteForm = {
  clienteId: string;
  from: string; // datetime-local (sin Z)
  to: string; // datetime-local (sin Z)
};

const emptyForm = (): ReporteForm => ({
  clienteId: '',
  from: '',
  to: '',
});

@Component({
  selector: 'app-reportes-page',
  standalone: true,
  imports: [CommonModule, AlertComponent, ButtonComponent, InputComponent, SelectComponent, TableComponent],
  templateUrl: './reportes.page.html',
  styleUrl: './reportes.page.css',
})
export class ReportesPage implements OnInit {
  constructor(private readonly sanitizer: DomSanitizer) {}
  loading = false;
  error: string | null = null;

  clientes: Cliente[] = [];
  form: ReporteForm = emptyForm();
  formErrors: Partial<Record<keyof ReporteForm, string>> = {};

  reporte: ReporteEstadoCuentaResponse | null = null;

  pdfUrl: string | null = null;
  pdfSafeUrl: SafeResourceUrl | null = null;

  columns: TableColumn<ReporteMovimientoItem>[] = [
    { header: 'Fecha', cell: (m) => this.fmtDate(m.fecha), width: '180px' },
    { header: 'Cuenta', cell: (m) => m.numeroCuenta, width: '150px' },
    { header: 'Tipo', cell: (m) => m.tipoMovimiento, width: '120px' },
    { header: 'Valor', cell: (m) => this.moneySigned(m.tipoMovimiento, m.valor), width: '140px' },
    { header: 'Saldo', cell: (m) => (m.saldoDisponible == null ? '-' : this.money(m.saldoDisponible)), width: '140px' },
    { header: 'Estado', cell: (m) => m.estado, width: '120px' },
  ];

  rowKey = (row: ReporteMovimientoItem) => row.movimientoId;

  async ngOnInit() {
    await this.loadClientes();
  }

  async loadClientes() {
    this.loading = true;
    this.error = null;
    try {
      this.clientes = await clientesApi.list();
    } catch (e: any) {
      this.error = e?.message ?? 'Error cargando clientes';
    } finally {
      this.loading = false;
    }
  }

  get clienteOptions(): SelectOption<string>[] {
    return this.clientes
      .slice()
      .sort((a, b) => a.nombre.localeCompare(b.nombre))
      .map((c) => ({ value: c.id, label: `${c.nombre} (${c.identificacion})` }));
  }

  setField<K extends keyof ReporteForm>(key: K, value: string) {
    (this.form[key] as any) = value;
    this.formErrors[key] = undefined;
  }

  private toIsoUtc(dtLocal: string): string {
    // Convierte 'YYYY-MM-DDTHH:mm' (local) a ISO UTC.
    const d = new Date(dtLocal);
    return d.toISOString();
  }

  private computeErrors(): Partial<Record<keyof ReporteForm, string>> {
    const errors: Partial<Record<keyof ReporteForm, string>> = {};
    if (!this.form.clienteId) errors.clienteId = 'El cliente es requerido';

    // Rango de fechas opcional:
    // - si no se envía, el backend retorna todos los movimientos del cliente.
    // - si se envía uno, exigimos el otro para evitar rangos incompletos.
    const hasFrom = Boolean(this.form.from);
    const hasTo = Boolean(this.form.to);

    if (hasFrom !== hasTo) {
      if (!hasFrom) errors.from = 'Si filtras por rango, "Desde" es requerido';
      if (!hasTo) errors.to = 'Si filtras por rango, "Hasta" es requerido';
    }

    if (hasFrom && hasTo) {
      const from = new Date(this.form.from).getTime();
      const to = new Date(this.form.to).getTime();
      if (!Number.isNaN(from) && !Number.isNaN(to) && from > to) {
        errors.to = 'La fecha hasta debe ser mayor o igual a desde';
      }
    }

    return errors;
  }

  get isFormValid() {
    return Object.keys(this.computeErrors()).length === 0;
  }

  private validate(): boolean {
    const errors = this.computeErrors();
    this.formErrors = errors;
    return Object.keys(errors).length === 0;
  }

  async consultar() {
    if (!this.validate()) return;

    this.loading = true;
    this.error = null;
    this.reporte = null;
    this.revokePdfUrl();

    try {
      const hasFrom = Boolean(this.form.from);
      const hasTo = Boolean(this.form.to);

      const res = await reportesApi.estadoCuenta({
        clienteId: this.form.clienteId,
        ...(hasFrom && hasTo
          ? {
              from: this.toIsoUtc(this.form.from),
              to: this.toIsoUtc(this.form.to),
            }
          : {}),
      });

      this.reporte = res;

      if (res.pdfBase64) {
        this.pdfUrl = this.base64PdfToObjectUrl(res.pdfBase64);
        // Para que Angular permita el preview en iframe sin bloquearlo por seguridad.
        this.pdfSafeUrl = this.sanitizer.bypassSecurityTrustResourceUrl(this.pdfUrl);
      }
    } catch (e: any) {
      this.error = e?.message ?? 'Error consultando el reporte';
    } finally {
      this.loading = false;
    }
  }

  descargarPdf() {
    if (!this.reporte?.pdfBase64) return;
    const url = this.base64PdfToObjectUrl(this.reporte.pdfBase64);
    const a = document.createElement('a');
    a.href = url;
    a.download = `estado-cuenta-${this.reporte.clienteId}.pdf`;
    a.click();
    setTimeout(() => URL.revokeObjectURL(url), 1500);
  }

  private base64PdfToObjectUrl(base64: string): string {
    const clean = base64.startsWith('data:') ? base64.split(',').pop() ?? '' : base64;
    const binary = atob(clean);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) bytes[i] = binary.charCodeAt(i);
    const blob = new Blob([bytes], { type: 'application/pdf' });
    return URL.createObjectURL(blob);
  }

  private revokePdfUrl() {
    if (this.pdfUrl) URL.revokeObjectURL(this.pdfUrl);
    this.pdfUrl = null;
    this.pdfSafeUrl = null;
  }

  fmtDate(iso: string) {
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) return iso;
    return new Intl.DateTimeFormat('es-EC', { dateStyle: 'short', timeStyle: 'short' }).format(d);
  }

  money(v: number) {
    const num = Number(v ?? 0);
    return new Intl.NumberFormat('es-EC', { style: 'currency', currency: 'USD' }).format(num);
  }

  moneySigned(tipoMovimiento: string, v: number) {
    const t = (tipoMovimiento ?? '').toUpperCase();
    const sign = t === 'DEBITO' ? -1 : 1;
    return this.money(sign * Number(v ?? 0));
  }

  get cuentas(): ReporteCuentaItem[] {
    return this.reporte?.cuentas ?? [];
  }

  get movimientos(): ReporteMovimientoItem[] {
    const movs: ReporteMovimientoItem[] = [];
    for (const c of this.cuentas) {
      for (const m of c.movimientos ?? []) movs.push(m);
    }

    // Orden más reciente primero
    return movs.sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());
  }

  trackCuenta(_: number, c: ReporteCuentaItem) {
    return c.cuentaId;
  }
}
