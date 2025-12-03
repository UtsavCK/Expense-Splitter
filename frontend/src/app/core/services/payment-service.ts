import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Payment } from '../models/payment';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/payments`;

  constructor(private http: HttpClient) {}

  getMyPayments(): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.apiUrl}/my-payments`);
  }

  createPayment(payment: any): Observable<Payment> {
    return this.http.post<Payment>(this.apiUrl, payment);
  }
}
