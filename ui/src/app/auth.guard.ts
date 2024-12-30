import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';
import {KeycloakAuthService} from './shared/services/keycloak.service';

@Injectable({
  providedIn: 'root'
})

export class AuthGuard implements CanActivate {
  constructor(private keycloakAuthService: KeycloakAuthService, private router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    if (this.keycloakAuthService.isAuthenticated()) {
      const userRoles = this.keycloakAuthService.getRoles();
      const requiredRole = next.data['roles'];

      if (userRoles.includes('admin')) {
        console.log("Admin role")
        return true;
      }

      if (requiredRole.includes('user') && userRoles.includes('user')) {
        console.log("User role");
        return true;
      }
      console.log("Log role");
      this.router.navigate(['/home']);
      return false;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }

}

