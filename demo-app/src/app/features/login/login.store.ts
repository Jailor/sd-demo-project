import { inject, Injectable, signal } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { catchError, finalize, Observable, of, tap } from 'rxjs';
import { LoginRequest, LoginResponse, LoginService } from '../../services/login.service';

interface AuthSnapshot {
  isAuthenticated: boolean;
  role: string | null;
}

const STORAGE_KEY = 'demo-app-auth';

@Injectable({ providedIn: 'root' })
export class LoginStore {
  private readonly loginService = inject(LoginService);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);
  readonly isAuthenticated = signal(false);
  readonly role = signal<string | null>(null);

  constructor() {
    this.restoreAuthState();
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    this.errorMessage.set(null);
    this.isSubmitting.set(true);

    return this.loginService.login(request).pipe(
      tap((response) => this.applyResponse(response)),
      catchError((error: unknown) => {
        const response = this.normalizeError(error);
        this.applyResponse(response);
        return of(response);
      }),
      finalize(() => this.isSubmitting.set(false)),
    );
  }

  logout(): void {
    this.isAuthenticated.set(false);
    this.role.set(null);
    localStorage.removeItem(STORAGE_KEY);
  }

  private applyResponse(response: LoginResponse): void {
    if (response.success) {
      this.isAuthenticated.set(true);
      this.role.set(response.role);
      this.saveAuthState();
    } else {
      this.isAuthenticated.set(false);
      this.role.set(null);
      this.errorMessage.set(response.errorMessage);
    }
  }

  private saveAuthState(): void {
    const snapshot: AuthSnapshot = {
      isAuthenticated: this.isAuthenticated(),
      role: this.role(),
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(snapshot));
  }

  private restoreAuthState(): void {
    const data = localStorage.getItem(STORAGE_KEY);
    if (data) {
      try {
        const snapshot: AuthSnapshot = JSON.parse(data);
        this.isAuthenticated.set(snapshot.isAuthenticated);
        this.role.set(snapshot.role);
      } catch {
        this.isAuthenticated.set(false);
        this.role.set(null);
      }
    }
  }

  private normalizeError(error: unknown): LoginResponse {
    if (error instanceof HttpErrorResponse && error.error) {
      return error.error as LoginResponse;
    }
    return {
      success: false,
      role: null,
      errorMessage: 'An unknown error occurred.',
    };
  }
}
