export interface Expense {
  expenseId: number;
  groupId: number;
  paidBy: number;
  paidByName: string;
  amount: number;
  description: string;
  expenseDate: string;
  createdAt: string;
  participants: ExpenseParticipant[];
}

export interface ExpenseParticipant {
  expenseParticipantId: number;
  userId: number;
  userName: string;
  shareAmount: number;
  splitType: string;
}
