import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/services/auth-service';
import { UserService } from '../../core/services/user-service';
import { GroupService } from '../../core/services/group-service';
import { PaymentService } from '../../core/services/payment-service';
import { BalanceService } from '../../core/services/balance-service';
import { SettlementService } from '../../core/services/settlement-service';
import { Group } from '../../core/models/group';
import { Payment } from '../../core/models/payment';
import { Balance } from '../../core/models/balance';
import { SettlementSuggestion } from '../../core/models/settlement';
import { UserStats } from '../../core/models/user';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {
  user: any = null;
  groups: Group[] = [];
  payments: Payment[] = [];
  balances: Balance[] = [];
  settlements: SettlementSuggestion[] = [];
  stats: UserStats | null = null;
  loading = true;
  activeTab = 'overview';
  
  showCreateGroupModal = false;
  showQuickPaymentModal = false;
  
  newGroupName = '';
  quickPayment = {
    paidTo: 0,
    paidToEmail: '',
    amount: 0,
    notes: ''
  };
  
  useEmailForPayment = true;

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private groupService: GroupService,
    private paymentService: PaymentService,
    private balanceService: BalanceService,
    private settlementService: SettlementService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.user = user;
      if (!user) {
        this.router.navigate(['/login']);
      }
    });
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    forkJoin({
      groups: this.groupService.getMyGroups(),
      payments: this.paymentService.getMyPayments(),
      balances: this.balanceService.getMyBalances(),
      settlements: this.settlementService.getMySuggestions(),
      stats: this.userService.getMyStats()
    }).subscribe({
      next: (data) => {
        this.groups = data.groups;
        this.payments = data.payments;
        this.balances = data.balances;
        this.settlements = data.settlements;
        this.stats = data.stats;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading data:', error);
        this.showNotification('Failed to load data', 'error');
        this.loading = false;
      }
    });
  }

  logout(): void {
    this.authService.logout();
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  openCreateGroupModal(): void {
    this.showCreateGroupModal = true;
    this.newGroupName = '';
  }

  closeCreateGroupModal(): void {
    this.showCreateGroupModal = false;
    this.newGroupName = '';
  }

  createGroup(): void {
    if (!this.newGroupName.trim()) {
      this.showNotification('Please enter a group name', 'error');
      return;
    }

    this.groupService.createGroup(this.newGroupName).subscribe({
      next: (group) => {
        this.closeCreateGroupModal();
        this.showNotification(`Group "${group.name}" created successfully!`, 'success');
        this.loadData();
      },
      error: (error) => {
        console.error('Error creating group:', error);
        this.showNotification(error.error?.message || 'Failed to create group', 'error');
      }
    });
  }

  openQuickPaymentModal(): void {
    this.showQuickPaymentModal = true;
    this.quickPayment = { 
      paidTo: 0, 
      paidToEmail: '',
      amount: 0, 
      notes: '' 
    };
  }

  closeQuickPaymentModal(): void {
    this.showQuickPaymentModal = false;
    this.quickPayment = { 
      paidTo: 0, 
      paidToEmail: '',
      amount: 0, 
      notes: '' 
    };
  }

  createQuickPayment(): void {
    if ((!this.quickPayment.paidTo && !this.quickPayment.paidToEmail) || 
        this.quickPayment.amount <= 0) {
      this.showNotification('Please fill all required fields', 'error');
      return;
    }

    if (this.useEmailForPayment && this.quickPayment.paidToEmail) {
      this.userService.searchUsers(this.quickPayment.paidToEmail).subscribe({
        next: (results) => {
          if (results.length === 0) {
            this.showNotification('No user found with that email', 'error');
            return;
          }
          
          const payment = {
            paidBy: this.user.userId,
            paidTo: results[0].userId,
            amount: this.quickPayment.amount,
            paymentDate: new Date().toISOString().split('T')[0],
            notes: this.quickPayment.notes
          };

          this.recordPayment(payment);
        },
        error: (error) => {
          this.showNotification('Error searching for user', 'error');
        }
      });
    } else {
      const payment = {
        paidBy: this.user.userId,
        paidTo: this.quickPayment.paidTo,
        amount: this.quickPayment.amount,
        paymentDate: new Date().toISOString().split('T')[0],
        notes: this.quickPayment.notes
      };

      this.recordPayment(payment);
    }
  }

  private recordPayment(payment: any): void {
    this.paymentService.createPayment(payment).subscribe({
      next: () => {
        this.closeQuickPaymentModal();
        this.showNotification('Payment recorded successfully!', 'success');
        this.loadData();
      },
      error: (error) => {
        console.error('Error creating payment:', error);
        this.showNotification(error.error?.message || 'Failed to record payment', 'error');
      }
    });
  }

  viewGroup(groupId: number): void {
    this.router.navigate(['/groups', groupId]);
  }

  goToProfile(): void {
    this.router.navigate(['/profile']);
  }

  getBalanceColor(balance: Balance): string {
    if (balance.fromUserId === this.user?.userId) {
      return 'negative';
    } else {
      return 'positive';
    }
  }

  getBalanceText(balance: Balance): string {
    if (balance.fromUserId === this.user?.userId) {
      return `You owe ${balance.toUserName}`;
    } else {
      return `${balance.fromUserName} owes you`;
    }
  }

  isGroupSettled(group: Group): boolean {
    return group.isSettled || false;
  }

  getGroupStatusBadge(group: Group): string {
    return this.isGroupSettled(group) ? 'Settled' : 'Active';
  }

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