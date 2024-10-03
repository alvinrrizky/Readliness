import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface Customer {
  customerId: number;
  customerName: string;
  customerAddress: string;
  customerCode: string;
  customerPhone: string;
  isActive: number;
  lastOrderDate: Date;
  pic: string;
}

interface CustomerResponse {
  customerList: Customer[];
  pageCurrent: number;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
}

@Component({
  selector: 'app-display-customer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './display-customer.component.html',
  styleUrls: ['./display-customer.component.scss'],
})
export class DisplayCustomerComponent implements OnInit {
  customerResponse: CustomerResponse | null = null;
  private readonly apiUrlList =
    'http://localhost:8081/api/readliness/getcustomerlist';

  private readonly apiUrlDelete =
    'http://localhost:8081/api/readliness/deletecustomer';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.fetchCustomers(1);
  }

  deleteCustomers(id: number): void {
    if (confirm('Are you sure you want to delete this customer?')) {
      this.http
        .post<CustomerResponse>(this.apiUrlDelete + `?id=${id}`, {})
        .subscribe({
          next: (response) => {
            console.log('Customer deleted successfully', response);
            this.fetchCustomers(1);
          },
          error: (error) => {
            console.error('Error deleting customer', error);
          },
          complete: () => {
            console.log('Delete operation completed');
          },
        });
    }
  }

  fetchCustomers(page: number): void {
    const requestBody = {
      page: page,
      shortBy: 'customer_id',
      direction: 'asc',
      size: 7,
    };

    this.http.post<CustomerResponse>(this.apiUrlList, requestBody).subscribe({
      next: (data) => {
        this.customerResponse = data;
      },
      error: (error) => {
        console.error('Error fetching customers', error);
      },
    });
  }

  generatePageArray(totalPages: number, currentPage: number): number[] {
    const pageArray = [];
    let start = 1;
    let end = Math.min(totalPages, 10);

    if (currentPage > 6 && totalPages > 10) {
      start = Math.max(currentPage - 5, 1);
      end = Math.min(start + 9, totalPages);
    }

    if (end - start < 9 && totalPages > 10) {
      start = Math.max(totalPages - 9, 1);
      end = totalPages;
    }

    for (let i = start; i <= end; i++) {
      pageArray.push(i);
    }

    return pageArray;
  }

  inputPagePrev(page: number): number {
    return page - 1;
  }

  inputPageNext(page: number): number {
    return page + 1;
  }

  navigateToAddComponent(): void {
    this.router.navigate(['customer/add']);
  }
}