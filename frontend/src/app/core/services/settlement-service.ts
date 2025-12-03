import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GroupSettlementPlan, SettlementSuggestion } from '../models/settlement';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class SettlementService {
  private apiUrl = `${environment.apiUrl}/settlements`;

  constructor(private http: HttpClient) {}

  getSettlementPlan(groupId: number): Observable<GroupSettlementPlan> {
    return this.http.get<GroupSettlementPlan>(`${this.apiUrl}/group/${groupId}/plan`);
  }

  getMySuggestions(): Observable<SettlementSuggestion[]> {
    return this.http.get<SettlementSuggestion[]>(`${this.apiUrl}/my-suggestions`);
  }

  executeSettlement(groupId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/group/${groupId}/execute`, {});
  }
}
