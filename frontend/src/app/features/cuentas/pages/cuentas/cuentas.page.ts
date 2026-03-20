import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

import { cuentasApi } from '../../../../core/api/cuentas.api';
import { clientesApi } from '../../../../core/api/clientes.api';
import { Cuenta, CuentaCreateRequest, CuentaUpdateRequest } from '../../../../shared/models/cuenta.model';
import { Cliente } from '../../../../shared/models/cliente.model';
import { AlertComponent } from '../../../../shared/components/ui/alert/alert.component';
import { ButtonComponent } from '../../../../shared/components/ui/button/button.component';
import { InputComponent } from '../../../../shared/components/ui/input/input.component';
import { ModalComponent } from '../../../../shared/components/ui/modal/modal.component';
import { SelectOption, SelectComponent } from '../../../../shared/components/ui/select/select.component';
import { TableColumn, TableComponent } from '../../../../shared/components/ui/table/table.component';
import { TIPO_CUENTA_VALUES, TipoCuenta } from '../../../../shared/models/enums';

type CuentaForm = {
  numeroCuenta: string;
  tipo: TipoCuenta | '';
  saldoInicial: string; // input text/number
  estado: boolean;
  clienteId: string;
};

const emptyForm = (): CuentaForm => ({
  numeroCuenta: '',
  tipo: '',
  saldoInicial: '',
  estado: true,
  clienteId: '',
});

@Component({
  selector: 'app-cuentas-page',
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
  templateUrl: './cuentas.page.html',
  styleUrl: './cuentas.page.css',
})
export class CuentasPage implements OnInit {
  loading = false;
  saving = false;
  error: string | null = null;

  cuentas: Cuenta[] = [];
  clientes: Cliente[] = [];

  query = '';

  modalOpen = false;
  modalMode: 'create' | 'edit' = 'create';
  editingId: string | null = null;

  original: Cuenta | null = null;

  form: CuentaForm = emptyForm();
  formErrors: Partial<Record<keyof CuentaForm, string>> = {};

  tipoCuentaOptions: SelectOption<TipoCuenta>[] = TIPO_CUENTA_VALUES.map((v) => ({ value: v, label: v }));

  get clienteOptions(): SelectOption<string>[] {
    return this.clientes
      .slice()
      .sort((a, b) => a.nombre.localeCompare(b.nombre))
      .map((c) => ({ value: c.id, label: `${c.nombre} (${c.identificacion})` }));
  }

  columns: TableColumn<Cuenta & { clienteNombre?: string }>[] = [
    { header: 'Número', key: 'numeroCuenta', width: '140px' },
    { header: 'Tipo', cell: (c) => c.tipo, width: '120px' },
    { header: 'Saldo inicial', cell: (c) => this.money(c.saldoInicial), width: '140px' },
    { header: 'Saldo actual', cell: (c) => this.money(c.saldoActual), width: '140px' },
    { header: 'Cliente', cell: (c) => c.clienteNombre ?? c.clienteId, width: '260px' },
    { header: 'Estado', cell: (c) => (c.estado ? 'Activa' : 'Inactiva'), width: '120px' },
    { header: 'Acciones', cell: () => '', width: '160px' },
  ];

  rowKey = (row: Cuenta) => row.id;

  async ngOnInit() {
    await this.loadAll();
  }

  money(v: number) {
    const num = Number(v ?? 0);
    return new Intl.NumberFormat('es-EC', { style: 'currency', currency: 'USD' }).format(num);
  }

  get cuentasView(): (Cuenta & { clienteNombre?: string })[] {
    const byId = new Map(this.clientes.map((c) => [c.id, c.nombre]));
    // Omitimos cuentas cuyo cliente ya no existe (por ejemplo, cliente dado de baja/eliminado en backend)
    return this.cuentas
      .map((a) => ({ ...a, clienteNombre: byId.get(a.clienteId) }))
      .filter((a) => Boolean(a.clienteNombre));
  }

  get filtered(): (Cuenta & { clienteNombre?: string })[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.cuentasView;
    return this.cuentasView.filter((a) => {
      return (
        a.numeroCuenta.toLowerCase().includes(q) ||
        a.tipo.toLowerCase().includes(q) ||
        (a.clienteNombre ?? '').toLowerCase().includes(q)
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
      const [cuentas, clientes] = await Promise.all([cuentasApi.list(), clientesApi.list()]);
      this.cuentas = cuentas;
      this.clientes = clientes;
    } catch (e: any) {
      this.error = e?.message ?? 'Error cargando cuentas';
    } finally {
      this.loading = false;
    }
  }

  openCreate(prefillClienteId?: string) {
    this.modalMode = 'create';
    this.editingId = null;
    this.original = null;
    this.form = emptyForm();
    if (prefillClienteId) this.form.clienteId = prefillClienteId;
    this.formErrors = {};
    this.modalOpen = true;
  }

  openEdit(a: Cuenta) {
    this.modalMode = 'edit';
    this.editingId = a.id;
    this.original = a;
    this.form = {
      numeroCuenta: a.numeroCuenta,
      tipo: a.tipo,
      saldoInicial: String(a.saldoInicial),
      estado: a.estado,
      clienteId: a.clienteId,
    };
    this.formErrors = {};
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  toggleEstado() {
    this.form.estado = !this.form.estado;
  }

  setField<K extends keyof CuentaForm>(key: K, value: string) {
    if (key === 'estado') {
      this.form.estado = value === 'true';
      this.formErrors.estado = undefined;
      return;
    }
    (this.form[key] as any) = value;
    this.formErrors[key] = undefined;
  }

  computeErrors(): Partial<Record<keyof CuentaForm, string>> {
    const errors: Partial<Record<keyof CuentaForm, string>> = {};
    const saldo = Number(this.form.saldoInicial);

    if (!this.form.numeroCuenta.trim()) errors.numeroCuenta = 'El número de cuenta es requerido';
    if (this.form.numeroCuenta.trim() && this.form.numeroCuenta.trim().length > 20)
      errors.numeroCuenta = 'Máximo 20 caracteres';

    if (!this.form.tipo) errors.tipo = 'El tipo de cuenta es requerido';
    if (this.form.tipo && !TIPO_CUENTA_VALUES.includes(this.form.tipo as any)) errors.tipo = 'Tipo inválido';

    if (this.form.saldoInicial.trim() === '') errors.saldoInicial = 'El saldo inicial es requerido';
    if (this.form.saldoInicial.trim() !== '' && (Number.isNaN(saldo) || saldo < 0)) errors.saldoInicial = 'Debe ser >= 0';

    if (!this.form.clienteId) errors.clienteId = 'El cliente es requerido';

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
        const body: CuentaCreateRequest = {
          numeroCuenta: this.form.numeroCuenta.trim(),
          tipo: this.form.tipo as TipoCuenta,
          saldoInicial: Number(this.form.saldoInicial),
          estado: this.form.estado,
          clienteId: this.form.clienteId,
        };
        await cuentasApi.create(body);
      } else {
        const id = this.editingId;
        if (!id) throw new Error('Cuenta inválida');
        const original = this.original;
        if (!original) throw new Error('No se pudo determinar la cuenta original');

        const patchBase: Partial<CuentaUpdateRequest> = {};
        const numeroCuenta = this.form.numeroCuenta.trim();
        const tipo = this.form.tipo as TipoCuenta;
        const saldoInicial = Number(this.form.saldoInicial);

        if (numeroCuenta !== original.numeroCuenta) patchBase.numeroCuenta = numeroCuenta;
        if (tipo !== original.tipo) patchBase.tipo = tipo;
        if (!Number.isNaN(saldoInicial) && saldoInicial !== original.saldoInicial) patchBase.saldoInicial = saldoInicial;

        // estado requerido por backend incluso si no cambió
        const patch: CuentaUpdateRequest = {
          ...(patchBase as any),
          estado: this.form.estado,
        };

        await cuentasApi.update(id, patch);
      }

      this.modalOpen = false;
      await this.loadAll();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo guardar';
    } finally {
      this.saving = false;
    }
  }

  async remove(a: Cuenta) {
    const ok = confirm(`¿Eliminar la cuenta "${a.numeroCuenta}"?`);
    if (!ok) return;

    this.error = null;
    try {
      await cuentasApi.remove(a.id);
      await this.loadAll();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo eliminar';
    }
  }
}
