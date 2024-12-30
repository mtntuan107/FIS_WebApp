import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {DessertComponent} from '../dessert.component';
import {take} from 'rxjs';
import {DessertsService} from '../../services/dessert.service';
import {CategoryService} from '../../../categories/services/category.service';
import {CommonModule} from '@angular/common';
import {LoadingService} from '../../../../shared/services/loading.service';

@Component({
  selector: 'app-dessert-update',
  templateUrl: './dessert-update.component.html',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    CommonModule,
  ],
  styleUrls: ['./dessert-update.component.css']
})
export class DessertUpdateComponent implements OnInit {
  @Output() onUpdate = new EventEmitter<any>();
  dessert: any = null;
  dessertForm: FormGroup = new FormGroup({});
  categories: any[] = [];

  constructor(
    private dessertComponent: DessertComponent,
    private formBuilder: FormBuilder,
    private dessertService: DessertsService,
    private categoryService: CategoryService,
    public loadingService: LoadingService
  ) { }

  ngOnInit() {
    this.loadCategories();

    this.dessertForm = this.formBuilder.group({
      name: [this.dessert?.name, Validators.required],
      price: [this.dessert?.price, [Validators.required, Validators.min(0)]],
      categoryId: [this.dessert?.category?.id, Validators.required],
      image: [this.dessert?.imageUrl, Validators.required],
    });

    this.dessertComponent.dessertEdit$.pipe(take(1)).subscribe(dessert => {
      if (dessert) {
        this.dessert = dessert;
        this.updateFormWithDessert(dessert);
      }
    });
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

  updateFormWithDessert(dessert: any): void {
    this.dessertForm.patchValue({
      name: dessert.name,
      price: dessert.price,
      image: dessert.imageUrl, // Nếu có xử lý hình ảnh
      categoryId: dessert.category.id
    });
  }


  closeModal(): void {
    this.dessertComponent.modalUpdate(false);
  }

  onFileChange(event: any): void {
    const file = event.target.files[0];
    if (file) {
      console.log('Selected file:', file.name);
      this.dessertForm.patchValue({ image: file });

    }
  }



  submitUpdate(): void {
    this.loadingService.setLoading(true);
    const formData = new FormData();
    formData.append('name', this.dessertForm.get('name')?.value);
    formData.append('price', this.dessertForm.get('price')?.value);
    formData.append('categoryId', this.dessertForm.get('categoryId')?.value);

    const image = this.dessertForm.get('image')?.value;
    if (image) {
      formData.append('image', image);
    }

    this.dessertService.updateDessert(this.dessert.id, formData).subscribe(
      () => {

        this.dessertComponent.reload(true);
        console.log(this.dessertForm.value);
        this.dessertComponent.modalUpdate(false);
        this.loadingService.setLoading(false);
      },
      (err) => {
        console.error('Error updating dessert', err);
      }
    );

  }
}
