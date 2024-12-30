import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CategoryService} from '../../services/category.service';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';

@Component({
  selector: 'app-category-update',
  imports: [
    FormsModule,
    CommonModule,
  ],
  templateUrl: './category-update.component.html',
  styleUrl: './category-update.component.css'
})
export class CategoryUpdateComponent {
  @Input() categoryToEdit: any = null; // Dữ liệu category cần chỉnh sửa
  @Output() categoryUpdated = new EventEmitter<any>(); // EventEmitter để thông báo khi cập nhật xong
  constructor(private categoryService: CategoryService) {}

  closeModal() {
    this.categoryToEdit = null;
  }

  saveCategory() {
    if (this.categoryToEdit) {
      this.categoryService.updateCategory(this.categoryToEdit.id, this.categoryToEdit).subscribe(
        (updatedCategory) => {
          // Phát sự kiện cập nhật khi hoàn tất
          this.categoryUpdated.emit(updatedCategory);
          this.categoryToEdit = null; // Đóng form sau khi lưu
        },
        (err) => {
          console.log('Error updating category:', err);
        }
      );
    }
  }

}
