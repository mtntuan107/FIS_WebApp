import { Component, OnInit } from '@angular/core';
import { KeycloakAuthService } from '../../../shared/services/keycloak.service';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { FormsModule } from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-login',
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'], // Sửa từ styleUrl -> styleUrls
  standalone: true
})
export class LoginComponent implements OnInit {
  username: string = '';
  password: string = '';
  errorMessage: string = '';

  private apiUrl = 'http://localhost:8000'; // URL của API backend

  constructor(
    private keycloakAuthService: KeycloakAuthService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit() {}
  login(username: string, password: string) {
    const loginRequest = { username, password };
    return this.http.post<any>(`${this.apiUrl}/auth/login`, loginRequest);
  }
  checkStatus(): Promise<{ status: string }> {
    if (this.username) {
      const params = new HttpParams().set('username', this.username); // Tạo tham số GET

      return firstValueFrom(
        this.http.get<{ status: string }>(`${this.apiUrl}/users/check`, { params })
      ).then((response) => {
        console.log('User status:', response.status); // Log trạng thái JSON trả về
        return response;
      }).catch((error) => {
        console.error('Error checking user status:', error);
        throw new Error('Unable to check user status');
      });
    } else {
      return Promise.reject(new Error('Username is required.'));
    }
  }



  async onLogin() {
    if (this.username && this.password) {
      try {
        const state: { status: string } = await this.checkStatus(); // Gọi checkStatus để nhận phản hồi JSON

        if (state.status === 'updated') {
          const tokenResponse = await firstValueFrom(this.login(this.username, this.password));
          if (tokenResponse?.accessToken) {
            this.keycloakAuthService.login(tokenResponse.accessToken);
            this.keycloakAuthService.initKeycloak();
            this.router.navigate(['/dessert']);
          } else {
            this.errorMessage = 'Invalid credentials';
          }
        } else if (state.status === 'yet') {
          console.log('Profile update is required:', state.status);
          this.keycloakAuthService.updateProfile(this.username, this.password);
        }
      } catch (error: any) {
        console.error('Login failed:', error);
        this.errorMessage = error.message || 'Login failed. Please try again.';
      }
    } else {
      this.errorMessage = 'Username and password are required.';
    }
  }

  register(){
    this.router.navigate(['/register']);
  }

  forgotPassword(){
    window.location.href = `http://localhost:8080/realms/custom/login-actions/reset-credentials?client_id=custom&redirect_uri=${window.location.origin}&response_type=code&scope=openid`;
    // window.location.href = `http://localhost:8080/realms/custom/protocol/openid-connect/auth?client_id=custom&redirect_uri=${window.location.origin}&response_type=code&scope=openid&kc_action=FORGOT_PASSWORD`;
  }
}
