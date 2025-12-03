export interface User {
  userId: number;
  name: string;
  email: string;
  createdAt: string;
}

export interface AuthResponse {
  userId: number;
  name: string;
  email: string;
  token: string;
}

export interface UserSearchResult {
  userId: number;
  name: string;
  email: string;
}

export interface UserStats {
  totalGroups: number;
  totalExpenses: number;
  totalPaymentsMade: number;
  totalPaymentsReceived: number;
  totalPaid: number;
  totalOwed: number;
  netBalance: number;
}
