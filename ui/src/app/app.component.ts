import {Component, OnInit} from '@angular/core';
import { RouterOutlet } from '@angular/router';

import {CommonModule} from '@angular/common';
import {PostComponent} from './features/posts/components/post-list/post.component';
import {DessertComponent} from './features/desserts/components/dessert.component';
import {HeaderComponent} from './header/header.component';
import {CartsComponent} from './features/cart/carts.component';
import {KeycloakAuthService} from './shared/services/keycloak.service';
import {KeycloakService} from 'keycloak-angular';
import {LoginComponent} from './features/auth/login/login.component';
import {LoadingService} from './shared/services/loading.service';
import {AuthGuard} from './auth.guard';
import {AsideComponent} from './aside/aside.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, CommonModule, HeaderComponent, AsideComponent, CartsComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
  providers: [KeycloakAuthService, KeycloakService, AuthGuard],
})
export class AppComponent implements OnInit {
  title = 'ui';
  constructor(private keycloakAuthService: KeycloakAuthService, public loadingService: LoadingService) {
  }
  async ngOnInit() {
    await this.keycloakAuthService.initKeycloak();
  }
}
