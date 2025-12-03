import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserSearchResult, UserStats } from '../models/user';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateProfile(name: string, email: string, password?: string): Observable<User> {
    const body: any = { name, email };
    if (password) {
      body.password = password;
    }
    return this.http.put<User>(`${this.apiUrl}/me`, body);
  }

  deleteAccount(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/me`);
  }

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${this.apiUrl}/search`, {
      params: { query }
    });
  }

  getMyStats(): Observable<UserStats> {
    return this.http.get<UserStats>(`${this.apiUrl}/me/stats`);
  }
}