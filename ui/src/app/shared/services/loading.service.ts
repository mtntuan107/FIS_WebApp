import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  loading: boolean = false;

  constructor() {}

  setLoading(state: boolean) {
    this.loading = state;
  }

  isLoading(): boolean {
    return this.loading;
  }
}

