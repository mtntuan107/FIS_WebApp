import {Injectable} from '@angular/core';
import {BehaviorSubject, catchError, Observable, of, Subject, tap} from 'rxjs';
import {ApiService} from '../../core/services/api.service';
import {HttpClient} from '@angular/common/http';


@Injectable({
  providedIn: 'root'
})

export class JasperService {
  private baseUrl = 'http://localhost:8000';

  constructor(
    private http: HttpClient,
  ){}

  importExcel(file: File, entityName: string): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('entityName', entityName);

    return this.http.post<string>(`${this.baseUrl}/import/import-excel`, formData, { responseType: 'text' as 'json' });
  }

}
