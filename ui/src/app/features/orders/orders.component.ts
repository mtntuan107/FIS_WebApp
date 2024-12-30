import {Component, OnInit} from '@angular/core';
import {OrderService} from '../../shared/services/order.service';
import {CommonModule} from '@angular/common';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {CamundaService} from '../../shared/services/camunda.service';
import {KeycloakAuthService} from '../../shared/services/keycloak.service';
import {KeycloakService} from 'keycloak-angular';
import {AuthGuard} from '../../auth.guard';

@Component({
  selector: 'app-orders',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './orders.component.html',
  styleUrl: './orders.component.css',
  providers: [KeycloakAuthService, KeycloakService, AuthGuard],
})
export class OrdersComponent implements OnInit {
  orders: any[] =[];
  details: any[] = [];
  detailModal: boolean = false;
  user: any = {};
  isLoading: boolean = false;
  processId: string = '';
  role: string = '';

  constructor(
    private orderService: OrderService,
    private camundaService: CamundaService,
    private keycloakAuthService: KeycloakAuthService,
    ) {}

  ngOnInit() {

    this.getUserFromLocalStorage();

    if(this.keycloakAuthService.getRoles().includes('admin')) {
      this.role = 'admin';
      this.loadOrders();
    }
    else if(this.keycloakAuthService.getRoles().includes('user')) {
      this.role = 'user';
      this.loadOrdersOfUser(this.user.id);
    }
  }

  getUserFromLocalStorage(): void {
    const user = localStorage.getItem('user');
    this.user = user ? JSON.parse(user) : {};
    console.log('User data:', this.user);
  }



  loadOrders() {
    this.orderService.getOrders().subscribe(
      (orders: any[]) => {
        this.orders = orders;
        console.log(this.orders.length);
      },
      (error) => {
        console.error('Error deleting orders:', error);
      })
  }
  loadDetails(orderId: number) {
    this.orderService.getOrderDetail(orderId).subscribe(
      (details: any) => {
        this.details = details;
        console.log(details);
      },
      (error) => {
        console.error('Error deleting details:', error);
      }
    )
  }
  loadOrdersOfUser(userId: number) {
    this.orderService.getOrdersOfUser(userId).subscribe(
      (orders: any[]) => {
        this.orders = orders;
        console.log(this.orders.length);
      },
      (err) => {
        console.error('Error deleting orders:', err);
      }
    )
  }

  showDetailModal(orderId: number) {
    this.detailModal = true;
    this.loadDetails(orderId);
  }
  getTotalPrice(): number {
    return this.details.reduce((total, item) => total + (item.dessert.price * item.quantity), 0);
  }
  updateOrderStatus(orderId: number, optionState: boolean): void {
    this.orderService.updateOrder(orderId, optionState).subscribe({
      next: async (updatedOrder) => {
        if (updatedOrder) {
          if (!optionState) {
            await this.getProcessId(orderId);
            this.claim(this.processId, 'pending', 'user');
            this.isLoading = true;
            await this.delay(2000);
            this.completeTask(this.processId,'pending','user','cancel');
            this.isLoading = false;
          }
          if(optionState) {
            await this.getStatus(orderId);
            console.log(this.orderStatus);
            if(this.orderStatus == 'PROCESSING'){
              console.log("PROCESSING")
              await this.getProcessId(orderId);
              this.claim(this.processId, 'pending', 'admin');
              this.isLoading = true;
              await this.delay(2000);
              this.completeTask(this.processId,'pending','admin','process');
              this.isLoading = false;
            }

            if(this.orderStatus == 'COMPLETED'){
              console.log("PROCESSING")
              await this.getProcessId(orderId);
              this.claim(this.processId, 'processing', 'admin');
              this.isLoading = true;
              await this.delay(2000);
              this.completeTaskForDelete(this.processId,'processing','admin');
              this.isLoading = false;
            }
          }
          this.loadOrders();
        } else {
          console.error('Order not found or update failed.');
        }
      },
      error: (err) => console.error('Error updating order:', err),
    });
  }

  claim(processInstanceId: string, taskDefinitionKey: string, assignee: string){
    if (!processInstanceId || !taskDefinitionKey) {
      console.error('ProcessInstanceId and TaskDefinitionKey are required');
      return;
    }
    this.camundaService.changeTaskStatus(processInstanceId, taskDefinitionKey, assignee).subscribe({
      next: () => {},
      error: (err) => {
        console.error('Error changing task status:', err);
      }
    });
  }
  completeTask(processInstanceId: string, taskDefinitionKey: string,assignee: string, status: string): void {
    if (!processInstanceId || !taskDefinitionKey || !status) {
      console.error('All parameters (processInstanceId, taskDefinitionKey, and status) must have values!');
      return;
    }

    const variables = { status: status };

    this.camundaService.completeTask(processInstanceId, taskDefinitionKey, assignee, variables).subscribe({
      next: () => {},
      error: (err) => {
        console.error('Error completing task:', err);
      }
    });
  }


  async getProcessId(orderId: number) {
    try {
      this.processId = await this.orderService.getProcessId(orderId).toPromise() || '';
    } catch (error) {
      this.processId = 'Order not found or an error occurred.';
    }
  }
  orderStatus: string = ''
  async getStatus(orderId: number) {
    try {
      this.orderStatus = await this.orderService.getStatus(orderId).toPromise() || '';
    } catch (error) {
      this.orderStatus = 'Order not found or an error occurred.';
    }
  }

  delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  completeTaskForDelete(processInstanceId: string, taskDefinitionKey: string,assignee: string): void {
    if (!processInstanceId || !taskDefinitionKey) {
      console.error('All parameters (processInstanceId, taskDefinitionKey, and status) must have values!');
      return;
    }

    const variables = {};

    this.camundaService.completeTask(processInstanceId, taskDefinitionKey, assignee, variables).subscribe({
      next: () => {},
      error: (err) => {
        console.error('Error completing task:', err);
      }
    });
  }
  async deleteOrder(orderId: number): Promise<void> {
    try {
      await this.getProcessId(orderId);
      await this.orderService.deleteOrder(orderId).toPromise();

      if (this.processId) {
        this.claim(this.processId, 'cancel', 'user');
        this.isLoading = true;
        await this.delay(2000);
        this.completeTaskForDelete(this.processId, 'cancel', 'user');
        this.isLoading = false;
        this.loadOrders();
      } else {
        console.error('Process ID is not available.');
      }
    } catch (err) {
      console.error('Error deleting order:', err);
    }
  }
}
