import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-profiles',
  imports: [],
  templateUrl: './profiles.component.html',
  styleUrl: './profiles.component.css'
})
export class ProfilesComponent implements OnInit {
  user: any = [];

  constructor() {

  }

  ngOnInit() {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const userObject = JSON.parse(storedUser);
      console.log(userObject);
      this.user = userObject;
    }
  }
  changePassword(){
    window.location.href="http://localhost:8080/realms/custom/protocol/openid-connect/auth?client_id=custom&redirect_uri=http://localhost:4200/profile&response_type=code&scope=openid&kc_action=UPDATE_PASSWORD&temporary=false";
  }
}
