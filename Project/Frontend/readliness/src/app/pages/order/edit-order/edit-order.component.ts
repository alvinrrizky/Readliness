import { Component } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { ErrorDialogComponent } from '../error-dialog/error-dialog.component';

interface OrderResponse {
  orderId: number;
  orderCode: string;
  orderDate: Date;
  totalPrice: number;
  quantity: number;
  customerId: number;
  customerName: string;
  itemsId: number;
  itemsName: string;
}

interface ItemResponse {
  itemsId: number;
  itemsName: string;
}

interface CustomerResponse {
  customerId: number;
  customerName: string;
}

interface ErrorResponse {
  messageError: string;
}

@Component({
  selector: 'app-edit-order',
  standalone: true,
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './edit-order.component.html',
  styleUrl: './edit-order.component.scss',
})
export class EditOrderComponent {
  itemResponse: ItemResponse[] = [];
  customerResponse: CustomerResponse[] = [];

  private readonly apiUrlUpdateOrder =
    'http://localhost:8081/api/readliness/updateorder';
  private readonly apiUrlgetItem =
    'http://localhost:8081/api/readliness/getitem';
  private readonly apiUrlgetCustomer =
    'http://localhost:8081/api/readliness/getcustomer';
  private readonly apiUrlDetailOrder =
    'http://localhost:8081/api/readliness/getdetailorder';

  updateOrderObj: {
    orderId: number;
    quantity: number;
    customerId: number;
    itemsId: number;
  } = {
    orderId: 0,
    quantity: 0,
    customerId: 0,
    itemsId: 0,
  };

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getCustomer();
    this.getItem();
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      if (id) {
        this.fetchDetailOrder(id);
      }
    });
  }

  fetchDetailOrder(id: number): void {
    this.http
      .post<OrderResponse>(this.apiUrlDetailOrder + `?id=${id}`, {})
      .subscribe({
        next: (data) => {
          this.updateOrderObj = {
            orderId: id,
            quantity: data.quantity,
            itemsId: data.itemsId,
            customerId: data.customerId,
          };
        },
        error: (error) => {
          console.error('Error fetching order', error);
        },
      });
  }

  updateOrder() {
    this.http.post(this.apiUrlUpdateOrder, this.updateOrderObj).subscribe({
      next: (response: any) => {
        console.log('Item Updated successfully', response);
        this.router.navigate(['/order/display']);
      },
      error: (error: HttpErrorResponse) => {
        const errorResponse = error.error as ErrorResponse;

        this.dialog.open(ErrorDialogComponent, {
          data: { messageError: errorResponse.messageError },
        });
      },
    });
  }

  getCustomer(): void {
    this.http.post<CustomerResponse[]>(this.apiUrlgetCustomer, {}).subscribe({
      next: (response) => {
        this.customerResponse = response;
      },
      error: (error) => {
        console.error('Error fetching customers', error);
      },
    });
  }

  getItem(): void {
    this.http.post<ItemResponse[]>(this.apiUrlgetItem, {}).subscribe({
      next: (response) => {
        this.itemResponse = response;
      },
      error: (error) => {
        console.error('Error fetching items', error);
      },
    });
  }
}
