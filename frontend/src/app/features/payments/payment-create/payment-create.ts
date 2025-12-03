import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PaymentService } from '../../../core/services/payment-service';
import { GroupService } from '../../../core/services/group-service';
import { AuthService } from '../../../core/services/auth-service';
import { Group, GroupMember } from '../../../core/models/group';

@Component({
  selector: 'app-payment-create',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './payment-create.html',
  styleUrls: ['./payment-create.css']
})
export class PaymentCreateComponent implements OnInit {
  groups: Group[] = [];
  selectedGroup: number = 0;
  members: GroupMember[] = [];
  loading = false;

  payment = {
    paidBy: 0,
    paidTo: 0,
    amount: 0,
    paymentDate: new Date().toISOString().split('T')[0],
    notes: ''
  };

  constructor(
    private paymentService: PaymentService,
    private groupService: GroupService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.payment.paidBy = currentUser.userId;
    }

    this.groupService.getMyGroups().subscribe({
      next: (groups) => {
        this.groups = groups;
      },
      error: (error) => {
        console.error('Error loading groups:', error);
      }
    });
  }

  onGroupChange(): void {
    if (this.selectedGroup) {
      this.groupService.getGroupMembers(this.selectedGroup).subscribe({
        next: (members) => {
          this.members = members;
        },
        error: (error) => {
          console.error('Error loading members:', error);
        }
      });
    } else {
      this.members = [];
    }
  }

  createPayment(): void {
    if (!this.payment.paidTo || this.payment.amount <= 0) {
      alert('Please fill all required fields');
      return;
    }

    this.loading = true;
    this.paymentService.createPayment(this.payment).subscribe({
      next: () => {
        alert('Payment recorded successfully!');
        this.router.navigate(['/']);
      },
      error: (error) => {
        console.error('Error creating payment:', error);
        alert('Failed to create payment: ' + (error.error?.message || 'Unknown error'));
        this.loading = false;
      }
    });
  }
}