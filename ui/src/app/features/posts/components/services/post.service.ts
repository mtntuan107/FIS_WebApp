// src/app/features/posts/services/post.service.ts
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {ApiService} from '../../../../core/services/api.service';

@Injectable({
  providedIn: 'root',
})
export class PostService {
  private baseUrl = 'https://jsonplaceholder.typicode.com/posts';

  constructor(private api: ApiService) {}

  getPosts(): Observable<any[]> {
    return this.api.get<any[]>(this.baseUrl);
  }

  getPostById(id: number): Observable<any> {
    return this.api.get<any>(`${this.baseUrl}/${id}`);
  }

  createPost(postData: any): Observable<any> {
    return this.api.post<any>(this.baseUrl, postData);
  }

  updatePost(id: number, postData: any): Observable<any> {
    return this.api.put<any>(`${this.baseUrl}/${id}`, postData);
  }

  deletePost(id: number): Observable<any> {
    return this.api.delete<any>(`${this.baseUrl}/${id}`);
  }
}
