// src/app/features/posts/services/post.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {ApiService} from '../../../core/services/api.service';


@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private baseUrl = 'http://localhost:8000/category';

  constructor(private api: ApiService) {}

  getCategories(): Observable<any[]> {
    return this.api.get<any[]>(this.baseUrl);
  }


  createCategory(categoryData: any): Observable<any> {
    return this.api.post<any>(`${this.baseUrl}/create`, categoryData);
  }

  updateCategory(id: number, categoryData: any): Observable<any> {
    return this.api.put<any>(`${this.baseUrl}/${id}`, categoryData);
  }

  deleteCategory(id: number): Observable<any> {
    return this.api.delete<any>(`${this.baseUrl}/${id}`);
  }
}
