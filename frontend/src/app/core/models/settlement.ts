export interface SettlementSuggestion {
  fromUserId: number;
  fromUserName: string;
  toUserId: number;
  toUserName: string;
  amount: number;
  description: string;
}

export interface GroupSettlementPlan {
  groupId: number;
  groupName: string;
  suggestions: SettlementSuggestion[];
  originalTransactionCount: number;
  optimizedTransactionCount: number;
  summary: string;
}
