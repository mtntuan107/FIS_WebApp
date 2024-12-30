import {Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { catchError, Observable, of } from "rxjs";

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private orderUrl = "http://localhost:8000/order";
  private detailUrl = "http://localhost:8000/order-detail";

  constructor(private http: HttpClient) {
  }

  getOrders(): Observable<any> {
    return this.http.get(this.orderUrl).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    )
  }

  getOrdersOfUser(userId: number): Observable<any> {
    return this.http.get(`${this.orderUrl}/getByUser?userId=${userId}`).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    )
  }

  getOrderDetail(orderId: number): Observable<any> {
    return this.http.get(`${this.detailUrl}/order?orderId=${orderId}`).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    )
  }

  createOrder(userId: number): Observable<any> {
    const params = new HttpParams().set('userId', userId.toString());
    return this.http.post<any>(`${this.orderUrl}/add`, null, {params: params}).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    )
  }

  createDetail(orderId: number, dessertId: number, quantity: number): Observable<any> {
    const params = new HttpParams()
      .set('orderId', orderId.toString())
      .set('dessertId', dessertId.toString())
      .set('quantity', quantity.toString());
    return this.http.post<any>(`${this.detailUrl}/add`, null, {params: params}).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    )
  }
  updateOrder(orderId: number, optionState: boolean): Observable<any> {
    const url = `${this.orderUrl}`;
    const params = new HttpParams()
      .set('orderId', orderId)
      .set('optionState', optionState);

    return this.http.put<any>(url, null, { params }).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    );
  }
  getStatus(orderId: number): Observable<string> {
    const params = new HttpParams().set('orderId', orderId.toString());
    return this.http.get(`${this.orderUrl}/getStatus`, { params, responseType: 'text' });
  }
  getProcessId(orderId: number): Observable<string> {
    const params = new HttpParams().set('orderId', orderId.toString());
    return this.http.get(`${this.orderUrl}/getProcessId`, { params, responseType: 'text' });
  }

  deleteOrder(orderId: number): Observable<void> {
    const url = `${this.orderUrl}`;  // Adjust endpoint if needed
    const params = new HttpParams().set('orderId', orderId.toString());

    // @ts-ignore
    return this.http.delete<void>(url, { params }).pipe(
      catchError((error) => {
        console.error(error);
        return of([]);
      })
    );
  }
}


