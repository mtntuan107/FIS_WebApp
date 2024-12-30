import {Component, OnInit} from '@angular/core';

import {CommonModule} from '@angular/common';
import {SharedCartService} from '../../shared/services/cart.service';
import {OrderService} from '../../shared/services/order.service';
import {CamundaService} from '../../shared/services/camunda.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-cart',
  imports: [CommonModule],
  templateUrl: './carts.component.html',
  styleUrl: './carts.component.css'
})
export class CartsComponent implements OnInit {
  isCartVisible: boolean= false;
  cart: any[] = [];
  user: any = [];
  checkoutVisible: boolean = false;
  constructor(
    private sharedCartService: SharedCartService,
    private orderService: OrderService,
    private camundaService: CamundaService,
    private router: Router,
  ) {}
  changeState(): void {
    this.sharedCartService.currentIsCartState.subscribe((status) => {
      this.isCartVisible = status;
    });
  }
  ngOnInit() {
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      const userObject = JSON.parse(storedUser);
      console.log(userObject);
      this.user = userObject;
    }

    this.changeState()
    this.listCartItems();
    this.sharedCartService.getCartUpdatedListener().subscribe(() => {
      this.listCartItems();
    });


  }
  checkCartItems(): void {
    if (this.cart[0].items.length > 0) {
      this.checkoutVisible = true; // Bật nếu có items trong cart.
    } else {
      this.checkoutVisible = false; // Tắt nếu không có items.
    }
  }
  closeCart(){
    this.isCartVisible = !this.isCartVisible;
  }

  checkoutVisibleState(){
    if(this.cart.length > 0){
      this.checkoutVisible = true;
    }
    else
      this.checkoutVisible = false;
  }

  listCartItems(): void {
    this.sharedCartService.getCart().subscribe({
      next: results => {
        const parsedResults = this.sharedCartService.convertJsonStringToObject(results);
        this.cart = parsedResults?.items || [];
        this.checkoutVisibleState();
      },
      error: error => {
        console.log(error);
      }
    })
  }

  removeItem(dessertId: number): void {
    this.sharedCartService.removeFromCart(dessertId).subscribe({
      next: () => {
        this.listCartItems()
      },
      error: (err) => {
        console.error('Error removing item:', err);
      }
    });
  }

  clearCart(): void {
    this.sharedCartService.clearCart().subscribe({
      next: results => {
        this.cart = results?.items || [];
      },
      error: (err) => {}
    })
  }

  createDetail(orderId: number): void {
    if (this.cart.length > 0) {
      this.cart.forEach((cartItem, index) => {
        const dessertId = cartItem.dessert.id;
        const quantity = cartItem.quantity;
        this.orderService.createDetail(orderId, dessertId, quantity).subscribe({
          next: (response) => {
            console.log(`Detail created for item ${index + 1}:`, response);
          },
          error: (error) => {
            console.error(`Error creating detail for item ${index + 1}:`, error);
          }
        });
      });
    } else {
      console.log('Cart is empty.');
    }
  }

  startProcess(processKey: string, username: string, orderId: number) {
    this.camundaService.startProcess(processKey, username, orderId).subscribe({
      next: () => {},
      error: (err) => {
        console.error('Error deleting process:', err);
      }
    })
  }

  checkout(): void {
    this.orderService.createOrder(this.user.id).subscribe({
      next: (order) => {
        if(order){
          console.log(order);
          this.createDetail(order.id)
          this.startProcess('p',this.user.username,order.id);
          this.clearCart();
        }
      },
      error: (err) => {
        console.log(err);
      }
    })
  }
  continueShopping(): void {
    this.closeCart();
    this.router.navigate(['/dessert']);
  }
}
