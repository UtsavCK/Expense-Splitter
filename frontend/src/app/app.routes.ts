import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login/login')
      .then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./features/auth/register/register')
      .then(m => m.RegisterComponent)
  },
  {
    path: '',
    canActivate: [authGuard],
    loadComponent: () => import('./features/dashboard/dashboard')
      .then(m => m.DashboardComponent)
  },
  {
    path: 'groups/:id',
    canActivate: [authGuard],
    loadComponent: () => import('./features/groups/group-detail/group-detail')
      .then(m => m.GroupDetailComponent)
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/users/user-profile/user-profile')
      .then(m => m.UserProfileComponent)
  },
  {
    path: 'payments/new',
    canActivate: [authGuard],
    loadComponent: () => import('./features/payments/payment-create/payment-create')
      .then(m => m.PaymentCreateComponent)
  },
  { path: '**', redirectTo: '/login' }
];