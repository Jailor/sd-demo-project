import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Department {
  id: string;
  name: string;
}

export interface DepartmentCreateDTO {
  name: string;
}

@Injectable({ providedIn: 'root' })
export class DepartmentService {
  private readonly apiUrl = 'http://localhost:8082/department';

  constructor(private http: HttpClient) {}

  getDepartments(): Observable<Department[]> {
    return this.http.get<Department[]>(this.apiUrl);
  }

  getDepartmentById(id: string): Observable<Department> {
    return this.http.get<Department>(`${this.apiUrl}/${id}`);
  }

  addDepartment(dto: DepartmentCreateDTO): Observable<Department> {
    return this.http.post<Department>(this.apiUrl, dto);
  }

  updateDepartment(id: string, dto: DepartmentCreateDTO): Observable<Department> {
    return this.http.put<Department>(`${this.apiUrl}/${id}`, dto);
  }

  deleteDepartment(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
