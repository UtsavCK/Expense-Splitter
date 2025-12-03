import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Group, GroupMember } from '../models/group';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class GroupService {
  private apiUrl = `${environment.apiUrl}/groups`;

  constructor(private http: HttpClient) {}

  getMyGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.apiUrl}/my-groups`);
  }

  getGroupById(id: number): Observable<Group> {
    return this.http.get<Group>(`${this.apiUrl}/${id}`);
  }

  createGroup(name: string): Observable<Group> {
    return this.http.post<Group>(this.apiUrl, { name });
  }

  getGroupMembers(groupId: number): Observable<GroupMember[]> {
    return this.http.get<GroupMember[]>(`${this.apiUrl}/${groupId}/members`);
  }

  addMember(groupId: number, userId: number): Observable<GroupMember> {
    return this.http.post<GroupMember>(`${this.apiUrl}/${groupId}/members`, { userId });
  }

  addMemberByEmail(groupId: number, email: string): Observable<GroupMember> {
  return this.http.post<GroupMember>(
    `${this.apiUrl}/${groupId}/members/by-email`,
    { email }
  );
  }

  removeMember(groupId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${groupId}/members/${userId}`);
  }
}
