import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Person } from '../models/person.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = 'http://localhost:8080/person';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<Person[]> {
    return this.http.get<Person[]>(this.apiUrl);
  }

  getUser(id: string): Observable<Person> {
    return this.http.get<Person>(`${this.apiUrl}/${id}`);
  }

  addUser(user: any): Observable<Person> {
    return this.http.post<Person>(this.apiUrl, user);
  }

  updateUser(id: string, user: any): Observable<Person> {
    return this.http.put<Person>(`${this.apiUrl}/${id}`, user);
  }

  patchUser(id: string, user: any): Observable<Person> {
    return this.http.patch<Person>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
