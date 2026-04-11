import { ChangeDetectionStrategy, Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProjectService, Project, ProjectCreateDTO } from '../services/project.service';

@Component({
  selector: 'app-project-list-page',
  templateUrl: './project-list-page.component.html',
  styleUrls: ['./project-list-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class ProjectListPageComponent {
  protected readonly projects = signal<Project[]>([]);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly showForm = signal(false);
  protected readonly formData = signal<ProjectCreateDTO>({ title: '' });
  protected readonly editingId = signal<string | null>(null);

  private readonly projectService = inject(ProjectService);

  constructor() {
    this.loadProjects();
  }

  loadProjects() {
    this.isLoading.set(true);
    this.projectService.getProjects().subscribe({
      next: (data: Project[]) => {
        this.projects.set(data);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        this.errorMessage.set(err.error?.message || 'Failed to load projects');
        this.isLoading.set(false);
      }
    });
  }

  openForm(project?: Project) {
    if (project) {
      this.formData.set({ title: project.title });
      this.editingId.set(project.id);
    } else {
      this.formData.set({ title: '' });
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
      this.projectService.updateProject(this.editingId()!, dto).subscribe({
        next: () => {
          this.loadProjects();
          this.closeForm();
        },
        error: (err: any) => {
          this.errorMessage.set(err.error?.message || 'Update failed');
        }
      });
    } else {
      this.projectService.addProject(dto).subscribe({
        next: () => {
          this.loadProjects();
          this.closeForm();
        },
        error: (err: any) => {
          this.errorMessage.set(err.error?.message || 'Create failed');
        }
      });
    }
  }

  deleteProject(id: string) {
    if (!confirm('Delete this project?')) return;
    this.projectService.deleteProject(id).subscribe({
      next: () => this.loadProjects(),
      error: (err: any) => {
        this.errorMessage.set(err.error?.message || 'Delete failed');
      }
    });
  }
}
