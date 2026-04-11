import { ChangeDetectionStrategy, Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DepartmentService, Department, DepartmentCreateDTO } from '../services/department.service';

@Component({
  selector: 'app-department-list-page',
  templateUrl: './department-list-page.component.html',
  styleUrls: ['./department-list-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class DepartmentListPageComponent {
  protected readonly departments = signal<Department[]>([]);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly showForm = signal(false);
  protected readonly formData = signal<DepartmentCreateDTO>({ name: '' });
  protected readonly editingId = signal<string | null>(null);

  private readonly departmentService = inject(DepartmentService);

  constructor() {
    this.loadDepartments();
  }

  loadDepartments() {
    this.isLoading.set(true);
    this.departmentService.getDepartments().subscribe({
      next: (data: Department[]) => {
        this.departments.set(data);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.errorMessage.set(err.error?.message || 'Failed to load departments');
        this.isLoading.set(false);
      }
    });
  }

  openForm(dept?: Department) {
    if (dept) {
      this.formData.set({ name: dept.name });
      this.editingId.set(dept.id);
    } else {
      this.formData.set({ name: '' });
      this.editingId.set(null);
    }
    this.showForm.set(true);
    this.errorMessage.set(null);
  }

  closeForm() {
    this.showForm.set(false);
    this.errorMessage.set(null);
  }

  submitForm() {
    const dto = this.formData();
    if (this.editingId()) {
      this.departmentService.updateDepartment(this.editingId()!, dto).subscribe({
        next: () => {
          this.loadDepartments();
          this.closeForm();
        },
        error: (err: any) => {
          this.errorMessage.set(err.error?.message || 'Update failed');
        }
      });
    } else {
      this.departmentService.addDepartment(dto).subscribe({
        next: () => {
          this.loadDepartments();
          this.closeForm();
        },
        error: (err: any) => {
          this.errorMessage.set(err.error?.message || 'Create failed');
        }
      });
    }
  }

  deleteDepartment(id: string) {
    if (!confirm('Delete this department?')) return;
    this.departmentService.deleteDepartment(id).subscribe({
      next: () => this.loadDepartments(),
      error: (err: any) => {
        this.errorMessage.set(err.error?.message || 'Delete failed');
      }
    });
  }
}
