import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth-service';
import { UserService } from '../../../core/services/user-service';
import { User } from '../../../core/models/user';

@Component({
  selector: 'app-user-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-profile.html',
  styleUrls: ['./user-profile.css'],
})
export class UserProfileComponent implements OnInit {
  user: User | null = null;
  loading = false;
  editing = false;
  canDelete = false;
  deletionReason = '';

  editForm = {
    name: '',
    email: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
    this.checkDeletionEligibility();
  }

  loadProfile(): void {
    this.userService.getMyProfile().subscribe({
      next: (user) => {
        this.user = user;
        this.editForm.name = user.name;
        this.editForm.email = user.email;
      },
      error: (error) => {
        console.error('Error loading profile:', error);
      },
    });
  }

  toggleEdit(): void {
    this.editing = !this.editing;
    if (!this.editing) {
      // Reset form
      if (this.user) {
        this.editForm.name = this.user.name;
        this.editForm.email = this.user.email;
        this.editForm.currentPassword = '';
        this.editForm.newPassword = '';
        this.editForm.confirmPassword = '';
      }
    }
  }

  saveProfile(): void {
    // Validate passwords match if changing password
    if (this.editForm.newPassword && this.editForm.newPassword !== this.editForm.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.loading = true;

    this.userService
      .updateProfile(
        this.editForm.name,
        this.editForm.email,
        this.editForm.newPassword || undefined
      )
      .subscribe({
        next: (updatedUser) => {
          this.user = updatedUser;
          this.editing = false;
          this.editForm.currentPassword = '';
          this.editForm.newPassword = '';
          this.editForm.confirmPassword = '';
          this.loading = false;
          alert('Profile updated successfully!');
        },
        error: (error) => {
          console.error('Error updating profile:', error);
          alert('Failed to update profile: ' + (error.error?.message || 'Unknown error'));
          this.loading = false;
        },
      });
  }

    checkDeletionEligibility(): void {
    this.userService.canDeleteAccount().subscribe({
      next: (eligibility) => {
        this.canDelete = eligibility.canDelete;
        this.deletionReason = eligibility.reason || '';
      },
      error: (error) => {
        console.error('Error checking deletion eligibility:', error);
        this.canDelete = false;
      }
    });
  }

  deleteAccount(): void {

    if (!this.canDelete) {
      this.showNotification(
        'Cannot delete account: ' + this.deletionReason,
        'error'
      );
      return;
    }

    const confirmation = prompt(
      'Are you sure you want to delete your account? Type "DELETE" to confirm.'
    );

    if (confirmation !== 'DELETE') {
      return;
    }

    this.loading = true;

    this.userService.deleteAccount().subscribe({
      next: () => {
        alert('Account deleted successfully');
        this.authService.logout();
      },
      error: (error) => {
        console.error('Error deleting account:', error);
        alert('Failed to delete account: ' + (error.error?.message || 'Unknown error'));
        this.loading = false;
      },
    });
  }

  logout(): void {
    this.authService.logout();
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
