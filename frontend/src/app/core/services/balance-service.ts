import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Balance, GroupBalanceSummary } from '../models/balance';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class BalanceService {
  private apiUrl = `${environment.apiUrl}/balances`;

  constructor(private http: HttpClient) {}

  getGroupBalances(groupId: number): Observable<GroupBalanceSummary> {
    return this.http.get<GroupBalanceSummary>(`${this.apiUrl}/group/${groupId}`);
  }

  getMyBalances(): Observable<Balance[]> {
    return this.http.get<Balance[]>(`${this.apiUrl}/my-balances`);
  }

  getBalanceWith(userId: number): Observable<Balance[]> {
    return this.http.get<Balance[]>(`${this.apiUrl}/with/${userId}`);
  }
}
