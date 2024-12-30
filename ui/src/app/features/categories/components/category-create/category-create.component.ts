import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';  // Removed unnecessary imports
import { CategoryService } from '../../services/category.service';
import {CategoriesComponent} from '../../categories.component';
import {CategoryListComponent} from '../category-list/category-list.component';

@Component({
  selector: 'app-category-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './category-create.component.html',
  styleUrls: ['./category-create.component.css']
})
export class CategoryCreateComponent implements OnInit {
  @Output() newCategory = new EventEmitter<any>();
  @Input() modal: boolean | undefined;
  @Output() close: EventEmitter<boolean> = new EventEmitter();

  categoryForm!: FormGroup;
  constructor(
    private formBuilder: FormBuilder,
    private categoryService: CategoryService,
    // private list: CategoryListComponent
  ) {}

  ngOnInit() {
    this.categoryForm = this.formBuilder.group({
      name: ['', [Validators.required]],
    });
  }

  closeModal() {
    this.close.emit(!this.modal);
  }

  createCategory() {
    if (this.categoryForm.invalid) {
      return;
    }
    const categoryData = this.categoryForm.value;
    this.categoryService.createCategory(categoryData).subscribe(
      (response) => {
        this.newCategory.emit(this.categoryForm.value);
        this.categoryForm.reset();
      },
      (error) => {
        console.error('Error:', error);
      }
    );
  }

}
