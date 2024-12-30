import {ChangeDetectorRef, Component, EventEmitter, Input, OnChanges, OnInit, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CategoryService} from '../../services/category.service';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-category-list',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './category-list.component.html',
  styleUrl: './category-list.component.css'
})
export class CategoryListComponent implements OnInit{
  @Input() categories: any[] = [];
  @Output() updateCategories = new EventEmitter<any[]>();
  @Output() editCategoryEvent = new EventEmitter<any>();
  constructor(
    private categoryService: CategoryService,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.loadCategories()
  }



  loadCategories() {
    this.categoryService.getCategories().subscribe(
      (categories: any[]) => {
        this.categories = categories;
        this.updateCategories.emit(this.categories);
      },
      (err) => {
        console.log(err);
      }
    )
  }


  deleteCategory(category: any) {
    this.categoryService.deleteCategory(category).subscribe(
      () => {
        this.loadCategories();
      },
      (err) => {console.log(err)}
    )
  }
  editCategory(category: any) {
    this.editCategoryEvent.emit(category); // Gửi dữ liệu danh mục cần sửa
  }
}
