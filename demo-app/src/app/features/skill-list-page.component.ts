import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { SkillService, Skill, SkillCreateDTO } from '../services/skill.service';

@Component({
  selector: 'app-skill-list-page',
  templateUrl: './skill-list-page.component.html',
  styleUrls: ['./skill-list-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class SkillListPageComponent {
  protected readonly skills = signal<Skill[]>([]);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly isLoading = signal(false);
  protected readonly showForm = signal(false);
  protected readonly formData = signal<SkillCreateDTO>({ name: '' });
  protected readonly editingId = signal<string | null>(null);

  constructor(private skillService: SkillService) {
    this.loadSkills();
  }

  loadSkills() {
    this.isLoading.set(true);
    this.skillService.getSkills().subscribe({
      next: (data) => {
        this.skills.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Failed to load skills');
        this.isLoading.set(false);
      }
    });
  }

  openForm(skill?: Skill) {
    if (skill) {
      this.formData.set({ name: skill.name });
      this.editingId.set(skill.id);
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
      this.skillService.updateSkill(this.editingId()!, dto).subscribe({
        next: () => {
          this.loadSkills();
          this.closeForm();
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Update failed');
        }
      });
    } else {
      this.skillService.addSkill(dto).subscribe({
        next: () => {
          this.loadSkills();
          this.closeForm();
        },
        error: (err) => {
          this.errorMessage.set(err.error?.message || 'Create failed');
        }
      });
    }
  }

  deleteSkill(id: string) {
    if (!confirm('Delete this skill?')) return;
    this.skillService.deleteSkill(id).subscribe({
      next: () => this.loadSkills(),
      error: (err) => {
        this.errorMessage.set(err.error?.message || 'Delete failed');
      }
    });
  }
}
