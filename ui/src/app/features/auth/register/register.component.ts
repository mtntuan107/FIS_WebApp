import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {KeycloakAuthService} from '../../../shared/services/keycloak.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  private apiUrl = 'http://localhost:8000/auth/register-user';  // Đảm bảo đường dẫn đúng

  constructor(
    private router: Router,
    private http: HttpClient,
    private keycloakAuthService: KeycloakAuthService
  ) {}

  registerForm: any = {
    username: '',
    password: ''
  };

  registerUser(registerForm: any): Observable<any> {
    console.log("register User load");
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });
    return this.http.post<any>(this.apiUrl, registerForm, { headers });
  }



  register() {
    if (!this.registerForm.username || !this.registerForm.password) {
      console.error('Username or password cannot be empty');
      return;
    }
    console.log("register load");
    this.registerUser(this.registerForm).subscribe(
      response => {
        console.log('Registration successful:', response);
        alert('register successfully');
        this.router.navigate(['login/']);
      },
      error => {
        console.error('Error during registration:', error);
      }
    );
  }

  updateUsername(event: Event) {
    this.registerForm.username = (event.target as HTMLInputElement).value;
  }

  updatePassword(event: Event) {
    this.registerForm.password = (event.target as HTMLInputElement).value;
  }
  login() {
    this.router.navigate(['login/']);
  }
}

