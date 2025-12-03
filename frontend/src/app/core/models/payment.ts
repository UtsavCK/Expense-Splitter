export interface Payment {
  paymentId: number;
  paidBy: number;
  paidByName: string;
  paidTo: number;
  paidToName: string;
  amount: number;
  paymentDate: string;
  notes: string;
}
