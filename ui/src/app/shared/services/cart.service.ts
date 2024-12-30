import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, Observable, of, Subject, tap} from 'rxjs';
import {ApiService} from '../../core/services/api.service';
import {HttpClient} from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})

export class SharedCartService{
  private baseUrl = 'http://localhost:8000/cart';
  constructor(
    private http: HttpClient,
  ){}

  private isCartState = new BehaviorSubject<boolean>(false);

  currentIsCartState = this.isCartState.asObservable();

  changeIsCart(status: boolean) {
    this.isCartState.next(status);
  }

  addToCart(dessertId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/add/${dessertId}`, {responseType:'text' as 'json', withCredentials: true}).pipe(
      catchError((err) => {
        console.log(err);
        return of(null);
      }),
      tap(() => {
        this.notifyCartUpdated(); // Phát ra sự kiện cập nhật giỏ hàng
      })
    );
  }

  getCart(): Observable<any> {
    return this.http.get(`${this.baseUrl}/get`, {responseType:'text' as 'json', withCredentials: true}).pipe(
      catchError((err) => {
        console.log(err);
        return of(null);
      })
    );
  }

  convertJsonStringToObject(jsonString: string): any {
    try {
      return JSON.parse(jsonString);
    } catch (error) {
      console.error("Invalid JSON string:", error);
      return null;
    }
  }
  private cartUpdated = new Subject<void>();
  notifyCartUpdated(): void {
    this.cartUpdated.next();
  }
  getCartUpdatedListener(): Observable<void> {
    return this.cartUpdated.asObservable(); // Lắng nghe sự kiện cập nhật giỏ hàng
  }

  removeFromCart(dessertId: number): Observable<any> {
    const url = `${this.baseUrl}?dessertId=${dessertId}`;
    return this.http.delete<any>(url, { withCredentials: true }).pipe(
      catchError((error) => {
        console.error('Error removing from cart:', error);
        return of({ items: [] }); // Trả về giỏ hàng rỗng nếu có lỗi
      })
    );
  }
  clearCart(): Observable<any> {
    return this.http.delete<any>(`${this.baseUrl}/clear`, { withCredentials: true });
  }
}
