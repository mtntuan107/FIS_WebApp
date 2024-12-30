import {ChangeDetectorRef, Component} from '@angular/core';
import {CategoryListComponent} from './components/category-list/category-list.component';
import {CategoryCreateComponent} from './components/category-create/category-create.component';
import {CategoryService} from './services/category.service';
import {CommonModule, NgForOf, NgIf} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {CategoryUpdateComponent} from './components/category-update/category-update.component';

@Component({
  selector: 'app-categories',
  imports: [
    CategoryListComponent,
    CategoryCreateComponent,
    CommonModule,
    FormsModule,
    CategoryUpdateComponent,
  ],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent {
  constructor(private categoryService: CategoryService) {
  }
  categories: any[] = [];
  categoryToEdit: any = null;
  modalCreate: boolean = false;
  updateCategories(updateCategories: any[]): void {
    this.categories = updateCategories;
  }

  addCategories(category: any): void {
    this.categories.push(category);
  }
  editCategories(category: any): void {
    this.categoryToEdit = { ...category };
  }

  handleCategoryUpdate(updatedCategory: any): void {
    const index = this.categories.findIndex(category => category.id === updatedCategory.id);
    if (index !== -1) {
      this.categories[index] = updatedCategory;
    }
    this.categoryToEdit = null;
  }

  openCreateModal() {
    this.modalCreate = !this.modalCreate;
  }
  closeCreateModal(state: boolean): void {
    this.modalCreate = state;
  }

}
