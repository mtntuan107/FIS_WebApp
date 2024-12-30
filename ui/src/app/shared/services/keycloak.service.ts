import { KeycloakService } from 'keycloak-angular';
import { Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { catchError, map, Observable, of, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';

export interface UserResponse {
  id: number;
  firstname: string;
  lastname: string;
  username: string;
  email: string;
}

@Injectable({
  providedIn: 'root'
})

export class KeycloakAuthService {
  constructor(
    private keycloakService: KeycloakService,
    private router: Router,
    private http: HttpClient) {}

  username: string = "";

  async initKeycloak() {
    try {
      await this.keycloakService.init({
        config: {
          url: 'http://localhost:8080/',
          realm: 'custom',
          clientId: 'custom'
        },
        initOptions: {
          onLoad: 'check-sso',
          checkLoginIframe: false
        }
      });

      if(await this.getToken()){
        await this.getDecodedToken();
        this.fetchUserInfo();
      }
      console.log(this.getRoles())
      console.log(this.getUserIdRealm())
    } catch (error) {
      console.error('Failed to initialize Keycloak', error);
    }
  }

  async logout(): Promise<void> {
    try {
      const logoutUrl = `http://localhost:8080/realms/custom/protocol/openid-connect/logout?redirect_uri=${window.location.origin}`;
      localStorage.removeItem('access_token');
      this.clearKeycloakCookies();
      localStorage.clear();

      window.location.href = logoutUrl;

      this.keycloakService.logout("http://localhost:4200");

    } catch (error) {
      console.error('Logout failed:', error);
    }
  }


  login(token: string){
    localStorage.setItem('access_token', token);
    this.updateKeycloakToken(token);
  }

  async isLoggedIn(): Promise<boolean> {
    if(localStorage.getItem("access_token"))
      return true;
    return false;
  }

  async getToken(): Promise<string> {
    return localStorage.getItem("access_token") || "";
  }

  private updateKeycloakToken(token: string): void {
    const keycloakInstance = this.keycloakService.getKeycloakInstance();
    if (keycloakInstance) {
      keycloakInstance.token = token || "";
      localStorage.setItem("access_token", token);
    } else {
      console.error("Keycloak instance is not initialized.");
    }
  }

  clearKeycloakCookies(): void {
    const cookies = document.cookie.split(";");

    for (let i = 0; i < cookies.length; i++) {
      const cookie = cookies[i];
      const eqPos = cookie.indexOf("=");
      const name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
      document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT;path=/";
    }
  }

  getSession(session: string){
    sessionStorage.setItem('session_state', session);
  }

  isAdmin(): boolean {
    const token = localStorage.getItem("access_token");
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        const roles = decodedToken?.resource_access?.custom?.roles || [];
        return roles.includes('admin');
      } catch (error) {
        console.error('Error decoding token:', error);
        return false;
      }
    }
    return false;
  }

  getRoles(): string[] {
    const token = localStorage.getItem("access_token");
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);

        const roles = decodedToken?.realm_access?.roles || [];

        return roles;
      } catch (error) {
        console.error('Error decoding token:', error);
        return [];
      }
    }
    return [];
  }

  getUserIdRealm(): string | undefined {
    const token = localStorage.getItem("access_token");
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        const sub: string = decodedToken?.sub;
        if (sub) {
          // Tách chuỗi để lấy phần userId
          const userId = sub.split(":")[1];
          return userId;
        }
        return '';
      } catch (error) {
        console.error('Error decoding token:', error);
        return '';
      }
    }
    return '';
  }


  isUser(): boolean {
    const token = localStorage.getItem("access_token");
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        const roles = decodedToken?.resource_access?.custom?.roles || [];
        return roles.includes('user');
      } catch (error) {
        console.error('Error decoding token:', error);
        return false;
      }
    }
    return false;
  }

  getId(): string | null {
    const token = localStorage.getItem("access_token");
    if (token) {
      try {
        const decodedToken: any = jwtDecode(token);
        return decodedToken.sub || null;
      } catch (error) {
        console.error('Error decoding token:', error);
        return null;
      }
    }
    return null;
  }

  async getDecodedToken()  {
    try {
      const token = await this.getToken();
      const decodedToken: any = jwtDecode(token);
      this.username = decodedToken['preferred_username']
    } catch (error) {
      console.error('Error decoding token', error);
    }
  }

  getUser(username: string): Observable<UserResponse> {
    return this.http.get<UserResponse>(`http://localhost:8000/users/info?username=${username}`).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error);
    return throwError(() => new Error('Something went wrong; please try again later.'));
  }
  user?: UserResponse;
  fetchUserInfo() {
    this.getUser(this.username).subscribe({
      next: (data) => {
        this.user = data;
        localStorage.setItem('user', JSON.stringify(this.user))
      },
      error: (err) => {
        console.error(err);
      }
    });
  }
  updateProfile(username: string, password: string) {
    this.keycloakService.login({
      redirectUri: window.location.origin + '/home',
      idpHint: 'VERIFY_PROFILE',
      loginHint: username
    });
  }
  isAuthenticated(): boolean {
    return !!localStorage.getItem('user');
  }
}

