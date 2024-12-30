import {Component, OnInit} from '@angular/core';
import {DessertListComponent} from './dessert-list/dessert-list.component';
import {DessertCreateComponent} from './dessert-create/dessert-create.component';
import {DessertsService} from '../services/dessert.service';
import {DessertUpdateComponent} from './dessert-update/dessert-update.component';
import {KeycloakAuthService} from '../../../shared/services/keycloak.service';
import {CommonModule, NgIf} from '@angular/common';
import {BehaviorSubject, take} from 'rxjs';
import {FormsModule} from '@angular/forms';
import {JasperService} from '../../../shared/services/jasper.service';

@Component({
  selector: 'app-dessert',
  templateUrl: './dessert.component.html',
  imports: [DessertListComponent, DessertCreateComponent, DessertUpdateComponent, CommonModule, FormsModule],
})
export class DessertComponent implements OnInit {
  desserts: any[] = [];
  createModal: boolean = false;
  role: string = '';
  isJModal: boolean = false;

  constructor(
    private dessertsService: DessertsService,
    private keycloakAuthService: KeycloakAuthService,
    private jasperService: JasperService,
  ) {
  }

  ngOnInit() {
    this.loadDesserts();

    if (this.keycloakAuthService.getRoles().includes('admin')) {
      this.role = 'admin';
    } else if (this.keycloakAuthService.getRoles().includes('user')) {
      this.role = 'user';
    }
  }

  openjModal() {
    this.isJModal = true;
  }

  closejModal() {
    this.isJModal = false;
  }

  loadDesserts() {
    this.dessertsService.getDesserts().subscribe(
      (data: any[]) => {
        this.desserts = data;
      },
      (error) => {
        console.error('Error loading desserts:', error);
      }
    );
  }

  addDessert(dessert: any): void {
    if (dessert.image instanceof File) {
      const reader = new FileReader();
      reader.onload = (e) => {
        dessert.image = e.target?.result as string;
        this.desserts.push(dessert);
      };
      reader.readAsDataURL(dessert.image);
    } else {
      this.desserts.push(dessert);
    }
  }

  updateDesserts(updateDesserts: any[]): void {
    this.desserts = updateDesserts;
  }

  openModalCreate(): void {
    this.createModal = !this.createModal;
  }

  closeModalCreate(state: boolean): void {
    this.createModal = state;
  }

  protected readonly open = open;

  private updateModal = new BehaviorSubject<boolean>(false);
  isOpenUpdateModal$ = this.updateModal.asObservable();

  private dessertEdit = new BehaviorSubject<any>(false);
  dessertEdit$ = this.dessertEdit.asObservable();

  openUpdateModal(isOpenUpdateModal: boolean, dessert: any): void {
    this.updateModal.next(isOpenUpdateModal);
    this.dessertEdit.next(dessert);
  }

  modalUpdate(status: boolean): void {
    this.updateModal.next(status);
    this.dessertEdit.next(null);
  }

  private reloadList = new BehaviorSubject<any>(false);
  reloadList$ = this.reloadList.asObservable();

  reload(status: boolean): void {

    this.reloadList.next(status);
  }

  baseURL: string = 'http://localhost:8000';
  sql: string = "SELECT d.name, d.price, c.name as category from desserts d inner join categories c on d.category_id = c.id";

  generateToPDF(): void {
    window.open(`${this.baseURL}/report/pdf?reportName=desserts&sql=${this.sql}`, '_blank');
  }

  generateToXLSX(): void {
    window.open(`${this.baseURL}/report/xlsx?reportName=desserts&sql=${this.sql}`,'_blank');
  }

  generateToDOCX(): void {
    window.open(`${this.baseURL}/report/docx?reportName=desserts&sql=${this.sql}`,'_blank');
  }

  generateModal: boolean = false;

  toggleModal(): void {
    this.generateModal = !this.generateModal;
  }

  file: File | null = null;
  entityName: string = '';
  message: string = '';
  showModal: boolean = false;
  openModal(): void {
    this.showModal = true; // Hiển thị modal
  }

  closeModal(): void {
    this.showModal = false; // Đóng modal
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.file = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.file) {
      this.jasperService.importExcel(this.file, 'Dessert').subscribe(
        (response) => {
          this.message = response;
          this.closeModal();
          alert('Import thành công!');
        },
        (error) => {
          this.message = 'Lỗi khi import dữ liệu: ' + error.error;
          alert(this.message);
        }
      );
    } else {
      alert('Vui lòng chọn file và nhập tên thực thể!');
    }
  }
}
