import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CamundaService {
  private readonly baseUrl = 'http://localhost:8000/camunda';

  constructor(private http: HttpClient) {}

  startProcess(processKey: string, username: string, orderId: number): Observable<string> {
    const params = new HttpParams()
      .set('processKey', processKey)
      .set('username', username)
      .set('orderId', orderId.toString());

    return this.http.post(`${this.baseUrl}/start-process`, null, { params, responseType: 'text' });
  }

  changeTaskStatus(processInstanceId: string, taskDefinitionKey: string, assignee?: string): Observable<string> {
    let params = new HttpParams()
      .set('processInstanceId', processInstanceId)
      .set('taskDefinitionKey', taskDefinitionKey);

    if (assignee) {
      params = params.set('assignee', assignee);
    }

    return this.http.put(`${this.baseUrl}/task/change-status`, null, { params, responseType: 'text' });
  }

  completeTask(processInstanceId: string, taskDefinitionKey: string, assignee: string, variables: Record<string, any>): Observable<any> {
    const params = new HttpParams()
      .set('processInstanceId', processInstanceId)
      .set('taskDefinitionKey', taskDefinitionKey)
      .set('assignee', assignee);

    return this.http.put(`${this.baseUrl}/task/complete`, variables, { params, responseType: 'text' });
  }
}
