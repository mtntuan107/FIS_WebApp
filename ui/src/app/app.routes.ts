import { Routes } from '@angular/router';
import {DessertComponent} from './features/desserts/components/dessert.component';
import {CategoriesComponent} from './features/categories/categories.component';
import {HomeComponent} from './features/home/home.component';
import {LoginComponent} from './features/auth/login/login.component';
import {RegisterComponent} from './features/auth/register/register.component';
import {OrdersComponent} from './features/orders/orders.component';
import {AuthGuard} from './auth.guard';
import {UsersComponent} from './features/users/users.component';
import {ProfilesComponent} from './features/profiles/profiles.component';

export const routes: Routes = [
  {path: 'dessert', component: DessertComponent},
  {path: 'category', component: CategoriesComponent, canActivate: [AuthGuard], data: { roles: ['admin']}},
  {path: 'home', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'order', component: OrdersComponent, canActivate: [AuthGuard], data: { roles: ['user']}},
  {path: 'user', component: UsersComponent, canActivate: [AuthGuard], data: { roles: ['user']}},
  {path: 'profile', component: ProfilesComponent, canActivate: [AuthGuard], data: { roles: ['user']}},
];
