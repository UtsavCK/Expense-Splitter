export interface Balance {
  fromUserId: number;
  fromUserName: string;
  toUserId: number;
  toUserName: string;
  amount: number;
}

export interface GroupBalanceSummary {
  groupId: number;
  groupName: string;
  balances: Balance[];
}
