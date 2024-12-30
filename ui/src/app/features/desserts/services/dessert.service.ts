// src/app/features/posts/services/post.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {ApiService} from '../../../core/services/api.service';
import {HttpClient} from '@angular/common/http';


@Injectable({
  providedIn: 'root',
})
export class DessertsService {
  private baseUrl = 'http://localhost:8000/dessert';

  constructor(private api: ApiService, private http: HttpClient) { }

  getDesserts(): Observable<any[]> {
    return this.api.get<any[]>(this.baseUrl);
  }

  createDessert(dessertData: any): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/create2`, dessertData);
  }

  updateDessert(id: number, dessertData: any): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/${id}`, dessertData);
  }

  deleteDessert(id: number): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/${id}`);
  }
}
