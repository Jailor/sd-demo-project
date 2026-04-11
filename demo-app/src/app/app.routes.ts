import { Routes } from '@angular/router';
import { authGuard, guestGuard } from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login',
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/login/login.component').then(
        (m) => m.LoginComponent,
      ),
    canActivate: [guestGuard],
  },
  {
    path: 'people',
    loadComponent: () =>
      import('./features/person-list/person-list-page.component').then(
        (m) => m.PersonListPageComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'users',
    loadComponent: () =>
      import('./features/user-list-page.component').then(
        (m) => m.UserListPageComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'departments',
    loadComponent: () =>
      import('./features/department-list-page.component').then(
        (m) => m.DepartmentListPageComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'projects',
    loadComponent: () =>
      import('./features/project-list-page.component').then(
        (m) => m.ProjectListPageComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'skills',
    loadComponent: () =>
      import('./features/skill-list-page.component').then(
        (m) => m.SkillListPageComponent,
      ),
    canActivate: [authGuard],
  },
  {
    path: 'error',
    loadComponent: () =>
      import('./features/not-found/not-found-page.component').then(
        (m) => m.NotFoundPageComponent,
      ),
  },
  {
    path: '**',
    redirectTo: 'error',
  },
];
