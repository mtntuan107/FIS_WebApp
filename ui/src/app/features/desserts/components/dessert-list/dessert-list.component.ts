import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DessertsService } from '../../services/dessert.service';
import { SharedCartService } from '../../../../shared/services/cart.service';
import {LoadingService} from '../../../../shared/services/loading.service';
import {KeycloakAuthService} from '../../../../shared/services/keycloak.service';
import {CategoryService} from '../../../categories/services/category.service';
import {DessertComponent} from '../dessert.component';
import {take} from 'rxjs';

@Component({
  selector: 'app-dessert-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './dessert-list.component.html',
  styleUrls: ['./dessert-list.component.css']
})
export class DessertListComponent implements OnInit {
  desserts: any[] = [];
  role: string = '';
  categories: any[] = [];
  selectedCategory: string = '';
  searchQuery: string = '';
  filteredDesserts: any[] = [];
  @Output() updateDesserts = new EventEmitter<any[]>();
  constructor(
    private dessertService: DessertsService,
    private cartService: SharedCartService,
    public loadingService: LoadingService,
    private keycloakAuthService: KeycloakAuthService,
    private categoryService: CategoryService,
    private dessertComponent: DessertComponent,
  ) {}

  ngOnInit() {
    this.loadDesserts();
    this.loadCategories();
    this.dessertComponent.reloadList$.subscribe(status => {
      console.log('Reload status:', status);

      if (status) {
        this.loadDesserts();

      }
    });
    if(this.keycloakAuthService.getRoles().includes('admin')) {
      this.role = 'admin';
    }
    else if(this.keycloakAuthService.getRoles().includes('user')) {
      this.role = 'user';
    }

  }



  startLoading() {
    this.loadingService.setLoading(true);
  }

  stopLoading() {
    this.loadingService.setLoading(false);
  }

  loadDesserts() {
    this.dessertService.getDesserts().subscribe(
      (data: any[]) => {
        this.desserts = data;
        this.filteredDesserts = data;
        this.updateDesserts.emit(this.desserts);
      },
      (error) => {
        console.error('Error loading desserts:', error);
      }
    );
  }

  loadCategories() {
    this.categoryService.getCategories().subscribe(
      (categories: any[]) => {
        this.categories = categories;
      },
      (err) => {
        console.error('Error loading categories', err);
      }
    );
  }

  addToCart(dessertId: number) {
    this.cartService.addToCart(dessertId).subscribe(
      {
        next: (data) => {
          console.log('Added to cart:', data);
        },
        error: (error) => {
          console.error('Error adding to cart:', error);
        }
      }
    );
  }

  filterDesserts() {
    this.filteredDesserts = this.desserts.filter(dessert => {
      const matchesCategory =
        !this.selectedCategory || dessert.category?.name === this.selectedCategory;
      const matchesSearch =
        !this.searchQuery || dessert.name.toLowerCase().includes(this.searchQuery.toLowerCase());
      return matchesCategory && matchesSearch;
    });
  }


  deleteDessert(dessertId: number) {
    this.dessertService.deleteDessert(dessertId).subscribe({
      next: (data) => {
        console.log('Dessert deleted:', data);
        this.loadDesserts();
      },
      error: (error) => {
        console.error('Error deleting dessert:', error);
      }
      }
    )
  }

  openUpdateDessert(dessert: any) {
    this.dessertComponent.openUpdateModal(true, dessert);
  }

}
