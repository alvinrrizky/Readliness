import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface OrderResponse {
  orderId: number;
  orderCode: string;
  orderDate: Date;
  totalPrice: number;
  quantity: number;
  customerName: string;
  itemsName: string;
}

@Component({
  selector: 'app-detail-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detail-order.component.html',
  styleUrl: './detail-order.component.scss',
})
export class DetailOrderComponent {
  orderResponse: OrderResponse | null = null;

  private readonly apiUrlDetailOrder =
    'http://localhost:8081/api/readliness/getdetailorder';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
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
          this.orderResponse = data;
        },
        error: (error) => {
          console.error('Error fetching order', error);
        },
      });
  }
}
