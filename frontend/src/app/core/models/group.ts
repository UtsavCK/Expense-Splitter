export interface Group {
  groupId: number;
  name: string;
  createdByUserId: number;
  createdAt: string;
}

export interface GroupMember {
  groupMemberId: number;
  groupId: number;
  userId: number;
  userName: string;
  userEmail: string;       
  joinedAt: string;
}
