import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Skill {
  id: string;
  name: string;
}

export interface SkillCreateDTO {
  name: string;
}

@Injectable({ providedIn: 'root' })
export class SkillService {
  private readonly apiUrl = '/skill';

  constructor(private http: HttpClient) {}

  getSkills(): Observable<Skill[]> {
    return this.http.get<Skill[]>(this.apiUrl);
  }

  getSkillById(id: string): Observable<Skill> {
    return this.http.get<Skill>(`${this.apiUrl}/${id}`);
  }

  addSkill(dto: SkillCreateDTO): Observable<Skill> {
    return this.http.post<Skill>(this.apiUrl, dto);
  }

  updateSkill(id: string, dto: SkillCreateDTO): Observable<Skill> {
    return this.http.put<Skill>(`${this.apiUrl}/${id}`, dto);
  }

  deleteSkill(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
