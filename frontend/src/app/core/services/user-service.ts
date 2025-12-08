import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, UserSearchResult, UserStats } from '../models/user';
import { environment } from '../../../environments/environment.dev';
import { DeletionEligibilityDto } from '../models/deletionEligibility';

@Injectable({ providedIn: 'root' })
export class UserService {
  private apiUrl = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  updateProfile(
    name: string,
    email: string,
    currentPassword?: string,
    newPassword?: string
  ): Observable<User> {
    const body: any = { name, email };
    if (currentPassword) {
      body.currentPassword = currentPassword;
    }
    if (newPassword) {
      body.newPassword = newPassword;
    }
    return this.http.put<User>(`${this.apiUrl}/me`, body);
  }

  canDeleteAccount(): Observable<DeletionEligibilityDto> {
    return this.http.get<DeletionEligibilityDto>(`${this.apiUrl}/me/can-delete`);
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