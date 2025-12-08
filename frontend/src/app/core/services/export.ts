import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.dev';

@Injectable({ providedIn: 'root' })
export class ExportService {
  private apiUrl = `${environment.apiUrl}/groups`;

  constructor(private http: HttpClient) {}

  /**
   * Download balances CSV for a group
   * FIX: Use responseType: 'blob' to get binary data
   */
  exportBalancesAsCSV(groupId: number): Observable<Blob> {
    return this.http.get<Blob>(
      `${this.apiUrl}/${groupId}/export/balances.csv`,
      { responseType: 'blob' as 'json' } // FIX: Proper blob handling
    );
  }

  /**
   * Download expenses CSV for a group
   */
  exportExpensesAsCSV(groupId: number): Observable<Blob> {
    return this.http.get<Blob>(`${this.apiUrl}/${groupId}/export/expenses.csv`, {
      responseType: 'blob' as 'json',
    });
  }

  /**
   * Download summary CSV for a group
   */
  exportSummaryAsCSV(groupId: number): Observable<Blob> {
    return this.http.get<Blob>(`${this.apiUrl}/${groupId}/export/summary.csv`, {
      responseType: 'blob' as 'json',
    });
  }

  /**
   * Helper method to trigger file download
   * FIX: Properly handle blob download with cleanup
   */
  downloadFile(blob: Blob, fileName: string): void {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = fileName;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);

    // FIX: Cleanup to prevent memory leaks
    setTimeout(() => {
      window.URL.revokeObjectURL(url);
    }, 100);
  }
}
