import { Component, OnInit } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDialogModule } from '@angular/material/dialog';
import { ErrorDialogComponent } from '../error-dialog/error-dialog.component';

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
  selector: 'app-add-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-order.component.html',
  styleUrl: './add-order.component.scss',
})
export class AddOrderComponent implements OnInit {
  itemResponse: ItemResponse[] = [];
  customerResponse: CustomerResponse[] = [];

  private readonly apiUrlAddOrder =
    'http://localhost:8081/api/readliness/saveorder';
  private readonly apiUrlgetItem =
    'http://localhost:8081/api/readliness/getitem';
  private readonly apiUrlgetCustomer =
    'http://localhost:8081/api/readliness/getcustomer';

  addOrderObj: {
    quantity: number;
    customerId: number;
    itemsId: number;
  } = {
    quantity: 0,
    customerId: 0,
    itemsId: 0,
  };

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getCustomer();
    this.getItem();
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
  addOrder(): void {
    this.http.post(this.apiUrlAddOrder, this.addOrderObj).subscribe({
      next: (response) => {
        console.log('Order added successfully', response);
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
}
