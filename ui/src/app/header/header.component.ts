import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {SharedCartService} from '../shared/services/cart.service';
import {KeycloakAuthService} from '../shared/services/keycloak.service';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-header',
  imports: [CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  constructor(
    private sharedCartService: SharedCartService,
    private keycloakAuthService: KeycloakAuthService,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}
  currentRoute: string = '';
  role: string = ''
  isCartOpen = false;
  ngOnInit() {
    if(this.keycloakAuthService.getRoles().includes('admin')) {
      this.role = 'admin';
    }
    else if(this.keycloakAuthService.getRoles().includes('user')) {
      this.role = 'user';
    }
    this.router.events.subscribe(() => {
      this.currentRoute = this.router.url; // Cập nhật đường dẫn hiện tại
    });
  }
  toggleCart() {
    this.isCartOpen = true;
    this.sharedCartService.changeIsCart(this.isCartOpen);
  }

  logout() {
    this.keycloakAuthService.logout();
  }
  login() {
    this.router.navigate(['/login']);
  }
  dessert(){
    this.router.navigate(['/dessert']);
  }
  order(){
    this.router.navigate(['/order']);
  }
  category(){
    this.router.navigate(['/category']);
  }

  register(){
    this.router.navigate(['/register']);
  }
  isActive(route: string): boolean {
    return this.currentRoute.includes(route);
  }
  home(){
    this.router.navigate(['/home'])
  }

  profile(){
    this.router.navigate(['/profile'])
  }

}
