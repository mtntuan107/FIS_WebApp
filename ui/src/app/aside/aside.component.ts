import {Component, OnInit} from '@angular/core';
import {CommonModule, NgClass} from '@angular/common';
import {Router} from '@angular/router';
import {KeycloakAuthService} from '../shared/services/keycloak.service';


@Component({
  selector: 'app-aside',
  imports: [
    NgClass,
    CommonModule,
  ],
  templateUrl: './aside.component.html',
  styleUrl: './aside.component.css'
})
export class AsideComponent implements OnInit {
  constructor(private router: Router, private keycloakAuthService: KeycloakAuthService,) { }
  isOpen = false;
  role: string = '';
  ngOnInit() {
    if(this.keycloakAuthService.getRoles().includes('admin')) {
      this.role = 'admin';
    }
    else if(this.keycloakAuthService.getRoles().includes('user')) {
      this.role = 'user';
    }
  }
  // Hàm mở rộng sidebar khi di chuột vào
  expandSidebar() {
    this.isOpen = true;
  }

  // Hàm thu nhỏ sidebar khi di chuột ra
  collapseSidebar() {
    this.isOpen = false;
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
  logout() {
    this.keycloakAuthService.logout();
  }
}
