import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DessertsService } from '../../services/dessert.service';
import { CategoryService } from '../../../categories/services/category.service';
import {LoadingService} from '../../../../shared/services/loading.service';

@Component({
  selector: 'app-dessert-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dessert-create.component.html',
  styleUrls: ['./dessert-create.component.css'],
})
export class DessertCreateComponent implements OnInit {
  @Output() newDessert = new EventEmitter<any>();
  @Input() openModal : boolean | undefined;
  @Output() closeModal : EventEmitter<boolean> = new EventEmitter();
  dessertForm!: FormGroup;
  categories: any[] = [];
  imagePreview: string | ArrayBuffer | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private dessertsService: DessertsService,
    private categoryService: CategoryService,
    public loadingService: LoadingService
  ) {}

  ngOnInit() {

    this.dessertForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      categoryId: ['', [Validators.required]],
      price: ['', [Validators.required]],
      image: ['', [Validators.required]],
    });

    this.loadCategories();
  }

  close(){
    this.closeModal.emit(!this.openModal);
  }

  startLoading() {
    this.loadingService.setLoading(true);
  }

  stopLoading() {
    this.loadingService.setLoading(false);
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

  createDessert() {
    this.startLoading();
    if (this.dessertForm.invalid) {
      return;
    }
    const dessertData = this.dessertForm.value;

    const formData = new FormData();
    formData.append('name', dessertData.name);
    formData.append('categoryId', dessertData.categoryId);
    formData.append('price', dessertData.price);
    if (dessertData.image instanceof File) {
      formData.append('image', dessertData.image);
    }

    this.dessertsService.createDessert(formData).subscribe(
      () => {
        this.newDessert.emit(this.dessertForm.value);
        this.dessertForm.reset();
        this.imagePreview = null;
        this.stopLoading();
      },
      (error) => {
        console.error('Error creating dessert:', error);
      }
    );

  }

  onFileSelected(event: any) {
    const target = event.target as HTMLInputElement;
    if (target.files?.length) {
      const file = target.files[0];
      const reader = new FileReader();

      reader.onload = (e) => {
        this.imagePreview = e.target?.result as string;
      };

      reader.readAsDataURL(file); // Chuyển file thành URL

      this.dessertForm.patchValue({ image: file });
    }
  }

  protected readonly open = open;
}
