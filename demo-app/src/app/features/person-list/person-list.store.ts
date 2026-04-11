import { computed, inject, Injectable, signal } from '@angular/core';
import { finalize } from 'rxjs';
import { CreatePersonDto, Person, UpdatePersonDto } from '../../models/person.model';
import { PersonService } from '../../services/person.service';

@Injectable({ providedIn: 'root' })
export class PersonListStore {
  private readonly personService = inject(PersonService);
  private readonly pendingRequests = signal(0);

  readonly persons = signal<Person[]>([]);
  readonly hasError = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isLoading = computed(() => this.pendingRequests() > 0);

  private beginRequest(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  private endRequest(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  load(): void {
    this.hasError.set(false);
    this.errorMessage.set(null);
    this.beginRequest();
    this.personService
      .getAll()
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (data) => this.persons.set(data),
        error: (err) => {
          this.hasError.set(true);
          this.errorMessage.set(
            err?.error?.details || err?.error?.message || err?.message || 'Unknown error'
          );
        },
      });
  }

  create(dto: CreatePersonDto): void {
    this.hasError.set(false);
    this.errorMessage.set(null);
    this.beginRequest();
    this.personService
      .create(dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (created) => this.persons.update((list) => [...list, created]),
        error: (err) => {
          this.hasError.set(true);
          this.errorMessage.set(err?.error?.message || err?.message || 'Unknown error');
        },
      });
  }

  update(id: string, dto: Partial<CreatePersonDto>): void {
    this.hasError.set(false);
    this.errorMessage.set(null);
    this.beginRequest();
    this.personService
      .patch(id, dto)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: (updated) =>
          this.persons.update((list) =>
            list.map((person) => (person.id === updated.id ? updated : person)),
          ),
        error: (err) => {
          this.hasError.set(true);
          this.errorMessage.set(err?.error?.message || err?.message || 'Unknown error');
        },
      });
  }

  remove(id: string): void {
    this.hasError.set(false);
    this.errorMessage.set(null);
    this.beginRequest();
    this.personService
      .delete(id)
      .pipe(finalize(() => this.endRequest()))
      .subscribe({
        next: () =>
          this.persons.update((list) => list.filter((person) => person.id !== id)),
        error: (err) => {
          this.hasError.set(true);
          this.errorMessage.set(err?.error?.message || err?.message || 'Unknown error');
        },
      });
  }
}
