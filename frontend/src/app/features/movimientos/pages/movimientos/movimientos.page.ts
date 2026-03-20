import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

import { movimientosApi } from '../../../../core/api/movimientos.api';
import { cuentasApi } from '../../../../core/api/cuentas.api';
import { clientesApi } from '../../../../core/api/clientes.api';

import { Movimiento, MovimientoCreateRequest, MovimientoUpdateRequest } from '../../../../shared/models/movimiento.model';
import { Cuenta } from '../../../../shared/models/cuenta.model';
import { Cliente } from '../../../../shared/models/cliente.model';
import { AlertComponent } from '../../../../shared/components/ui/alert/alert.component';
import { ButtonComponent } from '../../../../shared/components/ui/button/button.component';
import { InputComponent } from '../../../../shared/components/ui/input/input.component';
import { ModalComponent } from '../../../../shared/components/ui/modal/modal.component';
import { SelectOption, SelectComponent } from '../../../../shared/components/ui/select/select.component';
import { TableColumn, TableComponent } from '../../../../shared/components/ui/table/table.component';
import {
  ESTADO_MOVIMIENTO_VALUES,
  EstadoMovimiento,
  TIPO_MOVIMIENTO_VALUES,
  TipoMovimiento,
} from '../../../../shared/models/enums';

type MovimientoForm = {
  clienteId: string;
  cuentaId: string;
  tipo: TipoMovimiento | '';
  valor: string;
};

const emptyForm = (): MovimientoForm => ({
  clienteId: '',
  cuentaId: '',
  tipo: '',
  valor: '',
});

@Component({
  selector: 'app-movimientos-page',
  standalone: true,
  imports: [
    CommonModule,
    AlertComponent,
    ButtonComponent,
    InputComponent,
    SelectComponent,
    ModalComponent,
    TableComponent,
  ],
  templateUrl: './movimientos.page.html',
  styleUrl: './movimientos.page.css',
})
export class MovimientosPage implements OnInit {
  loading = false;
  saving = false;
  error: string | null = null;

  movimientos: Movimiento[] = [];
  cuentas: Cuenta[] = [];
  clientes: Cliente[] = [];

  query = '';

  modalOpen = false;
  modalMode: 'create' | 'edit' = 'create';
  editingId: string | null = null;
  original: Movimiento | null = null;

  form: MovimientoForm = emptyForm();
  formErrors: Partial<Record<keyof MovimientoForm, string>> = {};

  tipoOptions: SelectOption<TipoMovimiento>[] = TIPO_MOVIMIENTO_VALUES.map((v) => ({ value: v, label: v }));

  columns: TableColumn<Movimiento & { clienteNombre?: string; cuentaNumero?: string }>[] = [
    { header: 'Fecha', cell: (m) => this.fmtDate(m.fecha), width: '170px' },
    { header: 'Cliente', cell: (m) => m.clienteNombre ?? '-', width: '220px' },
    { header: 'Cuenta', cell: (m) => m.cuentaNumero ?? m.cuentaId, width: '150px' },
    { header: 'Tipo', cell: (m) => m.tipo, width: '120px' },
    { header: 'Valor', cell: (m) => this.moneySigned(m.tipo, m.valor), width: '140px' },
    { header: 'Saldo', cell: (m) => this.money(m.saldoDisponible), width: '140px' },
    { header: 'Estado', cell: (m) => m.estado, width: '120px' },
    { header: 'Acciones', cell: () => '', width: '160px' },
  ];

  rowKey = (row: Movimiento) => row.id;

  async ngOnInit() {
    await this.loadAll();
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

  moneySigned(tipo: TipoMovimiento, v: number) {
    const sign = tipo === 'DEBITO' ? -1 : 1;
    return this.money(sign * Number(v ?? 0));
  }

  get movimientosView(): (Movimiento & { clienteNombre?: string; cuentaNumero?: string })[] {
    const cuentaById = new Map(this.cuentas.map((c) => [c.id, c]));
    const clienteById = new Map(this.clientes.map((c) => [c.id, c]));
    return this.movimientos.map((m) => {
      const cuenta = cuentaById.get(m.cuentaId);
      const cliente = cuenta ? clienteById.get(cuenta.clienteId) : undefined;
      return {
        ...m,
        cuentaNumero: cuenta?.numeroCuenta,
        clienteNombre: cliente?.nombre,
      };
    });
  }

  get filtered(): (Movimiento & { clienteNombre?: string; cuentaNumero?: string })[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.movimientosView;
    return this.movimientosView.filter((m) => {
      return (
        (m.cuentaNumero ?? '').toLowerCase().includes(q) ||
        (m.clienteNombre ?? '').toLowerCase().includes(q) ||
        m.tipo.toLowerCase().includes(q) ||
        m.estado.toLowerCase().includes(q)
      );
    });
  }

  setQuery(v: string) {
    this.query = v;
  }

  async loadAll() {
    this.loading = true;
    this.error = null;
    try {
      const [movs, cuentas, clientes] = await Promise.all([
        movimientosApi.list(),
        cuentasApi.list(),
        clientesApi.list(),
      ]);
      this.movimientos = movs;
      this.cuentas = cuentas;
      this.clientes = clientes;
    } catch (e: any) {
      this.error = e?.message ?? 'Error cargando movimientos';
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

  get cuentaOptions(): SelectOption<string>[] {
    // Si selecciona cliente, filtramos sus cuentas para crear el movimiento más fácil.
    const cuentas = this.form.clienteId ? this.cuentas.filter((a) => a.clienteId === this.form.clienteId) : this.cuentas;
    return cuentas
      .slice()
      .sort((a, b) => a.numeroCuenta.localeCompare(b.numeroCuenta))
      .map((a) => ({ value: a.id, label: `${a.numeroCuenta} · ${a.tipo}` }));
  }

  openCreate() {
    this.modalMode = 'create';
    this.editingId = null;
    this.original = null;
    this.form = emptyForm();
    this.formErrors = {};
    this.modalOpen = true;
  }

  openEdit(m: Movimiento) {
    this.modalMode = 'edit';
    this.editingId = m.id;
    this.original = m;

    // Nota: el select de Cliente depende de que "cuentaOptions" se compute con base en form.clienteId.
    // Para que el cliente quede preseleccionado siempre, derivamos clienteId desde la cuenta.
    const cuenta = this.cuentas.find((a) => a.id === m.cuentaId);
    const clienteId = cuenta?.clienteId ?? '';

    this.form = {
      clienteId,
      cuentaId: m.cuentaId,
      tipo: m.tipo,
      valor: String(m.valor),
    };

    this.formErrors = {};
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  setField<K extends keyof MovimientoForm>(key: K, value: string) {
    (this.form[key] as any) = value;
    this.formErrors[key] = undefined;

    // si cambia cliente, resetea cuenta si ya no pertenece
    if (key === 'clienteId') {
      const ok = this.cuentas.some((a) => a.id === this.form.cuentaId && a.clienteId === value);
      if (!ok) this.form.cuentaId = '';
    }
  }

  computeErrors(): Partial<Record<keyof MovimientoForm, string>> {
    const errors: Partial<Record<keyof MovimientoForm, string>> = {};
    const valor = Number(this.form.valor);

    if (!this.form.clienteId) errors.clienteId = 'El cliente es requerido';
    if (!this.form.cuentaId) errors.cuentaId = 'La cuenta es requerida';

    if (!this.form.tipo) errors.tipo = 'El tipo es requerido';
    if (this.form.tipo && !TIPO_MOVIMIENTO_VALUES.includes(this.form.tipo as any)) errors.tipo = 'Tipo inválido';

    if (this.form.valor.trim() === '') errors.valor = 'El valor es requerido';
    if (this.form.valor.trim() !== '' && (Number.isNaN(valor) || valor <= 0)) errors.valor = 'Debe ser > 0';

    return errors;
  }

  get isFormValid(): boolean {
    return Object.keys(this.computeErrors()).length === 0;
  }

  validate(): boolean {
    const errors = this.computeErrors();
    this.formErrors = errors;
    return Object.keys(errors).length === 0;
  }

  async save() {
    if (!this.validate()) return;

    this.saving = true;
    this.error = null;

    try {
      if (this.modalMode === 'create') {
        const body: MovimientoCreateRequest = {
          cuentaId: this.form.cuentaId,
          tipo: this.form.tipo as TipoMovimiento,
          valor: Number(this.form.valor),
        };
        await movimientosApi.create(body);
      } else {
        const id = this.editingId;
        if (!id) throw new Error('Movimiento inválido');

        const patch: MovimientoUpdateRequest = {};
        if (this.form.tipo) patch.tipo = this.form.tipo as TipoMovimiento;

        const valor = Number(this.form.valor);
        if (!Number.isNaN(valor) && valor > 0) patch.valor = valor;

        await movimientosApi.update(id, patch);
      }

      this.modalOpen = false;
      await this.loadAll();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo guardar';
    } finally {
      this.saving = false;
    }
  }

  async remove(m: Movimiento) {
    const ok = confirm('¿Eliminar el movimiento?');
    if (!ok) return;

    this.error = null;
    try {
      await movimientosApi.remove(m.id);
      await this.loadAll();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo eliminar';
    }
  }
}
