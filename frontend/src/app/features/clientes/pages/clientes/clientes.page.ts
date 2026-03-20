import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

import { clientesApi } from '../../../../core/api/clientes.api';
import { Cliente, ClienteCreateRequest, ClienteUpdateRequest } from '../../../../shared/models/cliente.model';
import { AlertComponent } from '../../../../shared/components/ui/alert/alert.component';
import { ButtonComponent } from '../../../../shared/components/ui/button/button.component';
import { InputComponent } from '../../../../shared/components/ui/input/input.component';
import { ModalComponent } from '../../../../shared/components/ui/modal/modal.component';
import { TableColumn, TableComponent } from '../../../../shared/components/ui/table/table.component';
import { GENERO_VALUES, Genero } from '../../../../shared/models/enums';
import { SelectOption, SelectComponent } from '../../../../shared/components/ui/select/select.component';

type ClienteForm = {
  identificacion: string;
  nombre: string;
  genero: string;
  edad: string; // string para inputs
  direccion: string;
  telefono: string;
  password: string;
  estado: boolean;
};

const emptyForm = (): ClienteForm => ({
  identificacion: '',
  nombre: '',
  genero: '',
  edad: '',
  direccion: '',
  telefono: '',
  password: '',
  estado: true,
});

@Component({
  selector: 'app-clientes-page',
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
  templateUrl: './clientes.page.html',
  styleUrl: './clientes.page.css',
})
export class ClientesPage implements OnInit {
  loading = false;
  saving = false;
  error: string | null = null;

  clientes: Cliente[] = [];
  query = '';

  modalOpen = false;
  modalMode: 'create' | 'edit' = 'create';
  editingId: string | null = null;

  form: ClienteForm = emptyForm();
  formErrors: Partial<Record<keyof ClienteForm, string>> = {};

  columns: TableColumn<Cliente>[] = [
    { header: 'Identificación', key: 'identificacion', width: '140px' },
    { header: 'Nombre', key: 'nombre', width: '220px' },
    { header: 'Género', key: 'genero', width: '120px' },
    { header: 'Edad', cell: (c) => String(c.edad), width: '80px' },
    { header: 'Dirección', key: 'direccion', width: '260px' },
    { header: 'Teléfono', key: 'telefono', width: '140px' },
    { header: 'Estado', cell: (c) => (c.estado ? 'Activo' : 'Inactivo'), width: '110px' },
    // Acciones se renderizan como “chip-buttons” debajo en mobile, y como columna visual fuera del TableComponent.
    { header: 'Acciones', cell: () => '', width: '160px' },
  ];

  get filtered(): Cliente[] {
    const q = this.query.trim().toLowerCase();
    if (!q) return this.clientes;

    return this.clientes.filter((c) => {
      return (
        c.identificacion.toLowerCase().includes(q) ||
        c.nombre.toLowerCase().includes(q) ||
        c.telefono.toLowerCase().includes(q) ||
        c.direccion.toLowerCase().includes(q)
      );
    });
  }

  rowKey = (row: Cliente) => row.id;

  async ngOnInit() {
    await this.load();
  }

  async load() {
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

  openCreate() {
    this.modalMode = 'create';
    this.editingId = null;
    this.form = emptyForm();
    this.formErrors = {};
    this.modalOpen = true;
  }

  original: Cliente | null = null;

  openEdit(c: Cliente) {
    this.modalMode = 'edit';
    this.editingId = c.id;
    this.original = c;

    this.form = {
      identificacion: c.identificacion,
      nombre: c.nombre,
      genero: c.genero,
      edad: String(c.edad),
      direccion: c.direccion,
      telefono: c.telefono,
      password: '',
      estado: c.estado,
    };
    this.formErrors = {};
    this.modalOpen = true;
  }

  closeModal() {
    this.modalOpen = false;
  }

  setQuery(v: string) {
    this.query = v;
  }

  setField<K extends keyof ClienteForm>(key: K, value: string) {
    if (key === 'estado') {
      this.form.estado = value === 'true';
    } else {
      (this.form[key] as any) = value;
    }
    this.formErrors[key] = undefined;
  }

  toggleEstado() {
    this.form.estado = !this.form.estado;
  }

  generoOptions: SelectOption<Genero>[] = GENERO_VALUES.map((v) => ({ value: v, label: v }));

  computeErrors(): Partial<Record<keyof ClienteForm, string>> {
    const errors: Partial<Record<keyof ClienteForm, string>> = {};
    const edadNum = Number(this.form.edad);
    const genero = this.form.genero.trim();

    if (!this.form.identificacion.trim()) errors.identificacion = 'La identificación es requerida';
    if (!this.form.nombre.trim()) errors.nombre = 'El nombre es requerido';

    if (!genero) errors.genero = 'El género es requerido';
    if (genero && !GENERO_VALUES.includes(genero as any)) errors.genero = 'Género inválido';

    if (!this.form.edad.trim() || Number.isNaN(edadNum) || edadNum < 0 || edadNum > 150) errors.edad = 'Edad inválida';

    if (!this.form.direccion.trim()) errors.direccion = 'La dirección es requerida';
    if (!this.form.telefono.trim()) errors.telefono = 'El teléfono es requerido';

    if (this.modalMode === 'create' && !this.form.password.trim()) errors.password = 'La contraseña es requerida';
    if (this.form.password.trim() && this.form.password.trim().length < 4) errors.password = 'Mínimo 4 caracteres';

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
        const body: ClienteCreateRequest = {
          identificacion: this.form.identificacion.trim(),
          nombre: this.form.nombre.trim(),
          genero: this.form.genero.trim(),
          edad: Number(this.form.edad),
          direccion: this.form.direccion.trim(),
          telefono: this.form.telefono.trim(),
          password: this.form.password,
          estado: this.form.estado,
        };
        await clientesApi.create(body);
      } else {
        const id = this.editingId;
        if (!id) throw new Error('Cliente inválido');

        const original = this.original;
        if (!original) throw new Error('No se pudo determinar el cliente original');

        const patch: ClienteUpdateRequest = {};
        const nombre = this.form.nombre.trim();
        const genero = this.form.genero.trim();
        const edad = Number(this.form.edad);
        const direccion = this.form.direccion.trim();
        const telefono = this.form.telefono.trim();

        // PATCH: enviar solo campos modificados (backend permite null/ausente)
        if (nombre !== original.nombre) patch.nombre = nombre;
        if (genero !== original.genero) patch.genero = genero;
        if (!Number.isNaN(edad) && edad !== original.edad) patch.edad = edad;
        if (direccion !== original.direccion) patch.direccion = direccion;
        if (telefono !== original.telefono) patch.telefono = telefono;
        if (this.form.estado !== original.estado) patch.estado = this.form.estado;

        if (this.form.password.trim()) patch.password = this.form.password;

        if (Object.keys(patch).length === 0) {
          this.modalOpen = false;
          return;
        }

        await clientesApi.update(id, patch);
      }

      this.modalOpen = false;
      await this.load();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo guardar';
    } finally {
      this.saving = false;
    }
  }

  async remove(c: Cliente) {
    const ok = confirm(`¿Eliminar el cliente "${c.nombre}"?`);
    if (!ok) return;

    this.error = null;
    try {
      await clientesApi.remove(c.id);
      await this.load();
    } catch (e: any) {
      this.error = e?.message ?? 'No se pudo eliminar';
    }
  }
}
