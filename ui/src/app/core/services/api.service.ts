// src/app/core/services/api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root', // Service này sẽ được sử dụng toàn ứng dụng
})
export class ApiService {
  constructor(private http: HttpClient) {}

  /**
   * Gọi HTTP GET
   * @param url - Endpoint API
   * @param params - Tham số query (tùy chọn)
   * @returns Observable chứa dữ liệu phản hồi
   */
  get<T>(url: string, params?: HttpParams): Observable<T> {
    return this.http.get<T>(url, { params });
  }

  /**
   * Gọi HTTP POST
   * @param url - Endpoint API
   * @param body - Dữ liệu gửi đi
   * @param options - Tùy chọn headers (nếu cần)
   * @returns Observable chứa dữ liệu phản hồi
   */
  post<T>(url: string, body: any, options?: { headers?: HttpHeaders }): Observable<T> {
    return this.http.post<T>(url, body, options);
  }

  /**
   * Gọi HTTP PUT
   * @param url - Endpoint API
   * @param body - Dữ liệu gửi đi
   * @returns Observable chứa dữ liệu phản hồi
   */
  put<T>(url: string, body: any): Observable<T> {
    return this.http.put<T>(url, body);
  }

  /**
   * Gọi HTTP DELETE
   * @param url - Endpoint API
   * @param options - Tùy chọn headers (nếu cần)
   * @returns Observable chứa dữ liệu phản hồi
   */
  delete<T>(url: string, options?: { headers?: HttpHeaders }): Observable<T> {
    return this.http.delete<T>(url, options);
  }
}
