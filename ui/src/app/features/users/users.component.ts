import {Component, OnInit} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-users',
  imports: [
    FormsModule,
    CommonModule,
  ],
  templateUrl: './users.component.html',
  styleUrl: './users.component.css'
})
export class UsersComponent {
  changePassword(){
    window.location.href="http://localhost:8080/realms/custom/protocol/openid-connect/auth?client_id=custom&redirect_uri=http://localhost:4200&response_type=code&scope=openid&kc_action=UPDATE_PASSWORD";
  }
}
