import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface CustomerResponse {
  customerId: number;
  customerName: string;
  customerAddress: string;
  customerCode: string;
  customerPhone: string;
  isActive: number;
  lastOrderDate: Date;
  pic: string;
}

@Component({
  selector: 'app-detail-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detail-customer.component.html',
  styleUrl: './detail-customer.component.scss',
})
export class DetailCustomerComponent {
  customerResponse: CustomerResponse | null = null;

  private readonly apiUrlDetailItem =
    'http://localhost:8081/api/readliness/getdetailcustomer';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      if (id) {
        this.fetchDetailCustomer(id);
      }
    });
  }

  fetchDetailCustomer(id: number): void {
    this.http
      .post<CustomerResponse>(this.apiUrlDetailItem + `?id=${id}`, {})
      .subscribe({
        next: (data) => {
          this.customerResponse = data;
        },
        error: (error) => {
          console.error('Error fetching item', error);
        },
      });
  }
}
