import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth-service';
import { GroupService } from '../../../core/services/group-service';
import { ExpenseService } from '../../../core/services/expense-service';
import { BalanceService } from '../../../core/services/balance-service';
import { SettlementService } from '../../../core/services/settlement-service';
import { UserService } from '../../../core/services/user-service';
import { Group, GroupMember } from '../../../core/models/group';
import { Expense } from '../../../core/models/expense';
import { GroupBalanceSummary } from '../../../core/models/balance';
import { GroupSettlementPlan } from '../../../core/models/settlement';
import { User, UserSearchResult } from '../../../core/models/user';
import { forkJoin, debounceTime, distinctUntilChanged, Subject } from 'rxjs';

@Component({
  selector: 'app-group-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './group-detail.html',
  styleUrls: ['./group-detail.css']
})
export class GroupDetailComponent implements OnInit {
  groupId!: number;
  group: Group | null = null;
  user: User | null = null;
  members: GroupMember[] = [];
  expenses: Expense[] = [];
  balances: GroupBalanceSummary | null = null;
  settlementPlan: GroupSettlementPlan | null = null;
  loading = true;
  activeTab = 'expenses';
  
  // Modals
  showAddExpenseModal = false;
  showAddMemberModal = false;
  showConfirmModal = false;
  confirmModalData: any = null;
  
  // Add expense form
  newExpense = {
    description: '',
    amount: 0,
    paidBy: 0,
    expenseDate: new Date().toISOString().split('T')[0],
    participants: [] as { userId: number; userName: string; shareAmount: number; splitType: string }[]
  };

  // Add member form
  newMemberEmail = '';
  newMemberUserId = 0;
  useEmailForMember = true;
  userSearchResults: UserSearchResult[] = [];
  searchSubject = new Subject<string>();
  
  // Temp participant
  tempParticipant = {
    userId: 0,
    shareAmount: 0
  };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private groupService: GroupService,
    private expenseService: ExpenseService,
    private balanceService: BalanceService,
    private settlementService: SettlementService,
    private userService: UserService
  ) {
    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(query => {
      if (query && query.length >= 2) {
        this.searchUsers(query);
      } else {
        this.userSearchResults = [];
      }
    });
  }

  ngOnInit(): void {
    this.groupId = Number(this.route.snapshot.paramMap.get('id'));
    this.user = this.authService.getCurrentUser();
    this.loadGroupData();
  }

  loadGroupData(): void {
    this.loading = true;
    forkJoin({
      group: this.groupService.getGroupById(this.groupId),
      members: this.groupService.getGroupMembers(this.groupId),
      expenses: this.expenseService.getGroupExpenses(this.groupId),
      balances: this.balanceService.getGroupBalances(this.groupId),
      settlementPlan: this.settlementService.getSettlementPlan(this.groupId)
    }).subscribe({
      next: (data) => {
        this.group = data.group;
        this.members = data.members;
        this.expenses = data.expenses;
        this.balances = data.balances;
        this.settlementPlan = data.settlementPlan;
        this.loading = false;
        
        const currentUser = this.authService.getCurrentUser();
        if (currentUser) {
          this.newExpense.paidBy = currentUser.userId;
        }
      },
      error: (error) => {
        console.error('Error loading group data:', error);
        this.showNotification('Failed to load group data', 'error');
        this.loading = false;
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  openAddExpenseModal(): void {
    this.showAddExpenseModal = true;
  }

  closeAddExpenseModal(): void {
    this.showAddExpenseModal = false;
    this.resetExpenseForm();
  }

  resetExpenseForm(): void {
    const currentUser = this.authService.getCurrentUser();
    this.newExpense = {
      description: '',
      amount: 0,
      paidBy: currentUser?.userId || 0,
      expenseDate: new Date().toISOString().split('T')[0],
      participants: []
    };
    this.tempParticipant = { userId: 0, shareAmount: 0 };
  }

  addParticipant(): void {
    if (this.tempParticipant.userId && this.tempParticipant.shareAmount > 0) {
      const exists = this.newExpense.participants.find(p => p.userId === this.tempParticipant.userId);
      if (!exists) {
        const member = this.members.find(m => m.userId === this.tempParticipant.userId);
        this.newExpense.participants.push({
          userId: this.tempParticipant.userId,
          userName: member?.userName || `User ${this.tempParticipant.userId}`,
          shareAmount: this.tempParticipant.shareAmount,
          splitType: 'exact'
        });
        this.tempParticipant = { userId: 0, shareAmount: 0 };
      }
    }
  }

  removeParticipant(userId: number): void {
    this.newExpense.participants = this.newExpense.participants.filter(p => p.userId !== userId);
  }

  splitEqually(): void {
    if (this.members.length === 0 || this.newExpense.amount <= 0) return;
    
    const shareAmount = this.newExpense.amount / this.members.length;
    this.newExpense.participants = this.members.map(m => ({
      userId: m.userId,
      userName: m.userName,
      shareAmount: Number(shareAmount.toFixed(2)),
      splitType: 'equal'
    }));
  }

  addExpense(): void {
    if (!this.newExpense.description || this.newExpense.amount <= 0 || 
        this.newExpense.participants.length === 0) {
      this.showNotification('Please fill all fields and add at least one participant', 'error');
      return;
    }

    const expenseData = {
      groupId: this.groupId,
      description: this.newExpense.description,
      amount: this.newExpense.amount,
      paidBy: this.newExpense.paidBy,
      expenseDate: this.newExpense.expenseDate,
      participants: this.newExpense.participants.map(p => ({
        userId: p.userId,
        shareAmount: p.shareAmount,
        splitType: p.splitType
      }))
    };

    this.expenseService.createExpense(expenseData).subscribe({
      next: () => {
        this.closeAddExpenseModal();
        this.showNotification('Expense added successfully!', 'success');
        this.loadGroupData();
      },
      error: (error) => {
        console.error('Error creating expense:', error);
        this.showNotification(error.error?.message || 'Failed to add expense', 'error');
      }
    });
  }

  openAddMemberModal(): void {
    this.showAddMemberModal = true;
  }

  closeAddMemberModal(): void {
    this.showAddMemberModal = false;
    this.newMemberEmail = '';
    this.newMemberUserId = 0;
    this.userSearchResults = [];
  }

  onMemberSearchInput(event: any): void {
    const query = event.target.value;
    this.searchSubject.next(query);
  }

  searchUsers(query: string): void {
    this.userService.searchUsers(query).subscribe({
      next: (results) => {
        this.userSearchResults = results;
      },
      error: (error) => {
        console.error('Error searching users:', error);
      }
    });
  }

  selectUserFromSearch(user: UserSearchResult): void {
    this.newMemberEmail = user.email;
    this.userSearchResults = [];
  }

  addMember(): void {
    if (this.useEmailForMember) {
      if (!this.newMemberEmail) {
        this.showNotification('Please enter an email address', 'error');
        return;
      }

      this.groupService.addMemberByEmail(this.groupId, this.newMemberEmail).subscribe({
        next: (member) => {
          this.closeAddMemberModal();
          this.showNotification(`${member.userName} added to group!`, 'success');
          this.loadGroupData();
        },
        error: (error) => {
          console.error('Error adding member:', error);
          this.showNotification(error.error?.message || 'Failed to add member', 'error');
        }
      });
    } else {
      if (!this.newMemberUserId) {
        this.showNotification('Please enter a valid user ID', 'error');
        return;
      }

      this.groupService.addMember(this.groupId, this.newMemberUserId).subscribe({
        next: (member) => {
          this.closeAddMemberModal();
          this.showNotification(`Member added to group!`, 'success');
          this.loadGroupData();
        },
        error: (error) => {
          console.error('Error adding member:', error);
          this.showNotification(error.error?.message || 'Failed to add member', 'error');
        }
      });
    }
  }

  // ✅ FIXED: Better confirmation system
  confirmRemoveMember(userId: number, userName: string): void {
    const currentUser = this.authService.getCurrentUser();
    const isSelf = currentUser?.userId === userId;
    
    this.confirmModalData = {
      title: isSelf ? 'Leave Group?' : 'Remove Member?',
      message: isSelf 
        ? `Are you sure you want to leave "${this.group?.name}"?`
        : `Are you sure you want to remove ${userName} from this group?`,
      action: () => this.removeMember(userId, isSelf)
    };
    this.showConfirmModal = true;
  }

  removeMember(userId: number, isSelf: boolean): void {
    this.groupService.removeMember(this.groupId, userId).subscribe({
      next: () => {
        this.showConfirmModal = false;
        this.confirmModalData = null;
        
        if (isSelf) {
          this.showNotification('You have left the group', 'success');
          // ✅ FIXED: Navigate to dashboard instead of triggering logout
          setTimeout(() => this.router.navigate(['/']), 500);
        } else {
          this.showNotification('Member removed successfully', 'success');
          this.loadGroupData();
        }
      },
      error: (error) => {
        console.error('Error removing member:', error);
        this.showConfirmModal = false;
        this.showNotification(error.error?.message || 'Failed to remove member', 'error');
      }
    });
  }

  confirmExecuteSettlement(): void {
    this.confirmModalData = {
      title: 'Execute Settlement?',
      message: 'This will record all suggested payments to settle the group. This action cannot be undone.',
      action: () => this.executeSettlement()
    };
    this.showConfirmModal = true;
  }

  executeSettlement(): void {
    this.settlementService.executeSettlement(this.groupId).subscribe({
      next: () => {
        this.showConfirmModal = false;
        this.confirmModalData = null;
        this.showNotification('Settlement executed successfully!', 'success');
        this.loadGroupData();
      },
      error: (error) => {
        console.error('Error executing settlement:', error);
        this.showConfirmModal = false;
        this.showNotification(error.error?.message || 'Failed to execute settlement', 'error');
      }
    });
  }

  closeConfirmModal(): void {
    this.showConfirmModal = false;
    this.confirmModalData = null;
  }

  logout(): void {
    this.authService.logout();
  }

  // ✅ Toast notification system
  private showNotification(message: string, type: 'success' | 'error' | 'info' = 'info'): void {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => toast.classList.add('show'), 10);
    setTimeout(() => {
      toast.classList.remove('show');
      setTimeout(() => document.body.removeChild(toast), 300);
    }, 3000);
  }
}