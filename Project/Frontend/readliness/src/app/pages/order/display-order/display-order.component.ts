import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface Order {
  orderId: number;
  orderCode: string;
  orderDate: Date;
  totalPrice: number;
  quantity: number;
  customerName: string;
  itemsName: string;
}

interface OrderResponse {
  orderList: Order[];
  pageCurrent: number;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
}

@Component({
  selector: 'app-display-order',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './display-order.component.html',
  styleUrl: './display-order.component.scss',
})
export class DisplayOrderComponent {
  orderResponse: OrderResponse | null = null;
  pageSize: number = 5;
  page: number = 1;
  private readonly apiUrlList =
    'http://localhost:8081/api/readliness/getorderlist';

  private readonly apiUrlDelete =
    'http://localhost:8081/api/readliness/deleteorder';

  private readonly apiUrlDownloadPdf =
    'http://localhost:8081/api/readliness/downloadpdf';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.fetchOrder(this.page, this.pageSize);
  }

  deleteOrder(id: number): void {
    if (confirm('Are you sure you want to delete this order?')) {
      this.http
        .post<OrderResponse>(this.apiUrlDelete + `?id=${id}`, {})
        .subscribe({
          next: (response) => {
            console.log('Order deleted successfully', response);
            this.fetchOrder(this.page, this.pageSize);
          },
          error: (error) => {
            console.error('Error deleting order', error);
          },
          complete: () => {
            console.log('Delete operation completed');
          },
        });
    }
  }

  downloadPdf(): void {
    this.http.get(this.apiUrlDownloadPdf, { responseType: 'blob' }).subscribe({
      next: (blob) => {
        const blobUrl = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = blobUrl;
        a.download = 'orders_report.pdf';

        document.body.appendChild(a);

        a.click();

        document.body.removeChild(a);
        URL.revokeObjectURL(blobUrl);
      },
      error: (error) => {
        console.error('Error downloading PDF', error);
      },
    });
  }

  fetchOrder(page: number, size: number): void {
    const requestBody = {
      page: page,
      shortBy: 'order_id',
      direction: 'asc',
      size: size,
    };

    this.http.post<OrderResponse>(this.apiUrlList, requestBody).subscribe({
      next: (data) => {
        this.orderResponse = data;
      },
      error: (error) => {
        console.error('Error fetching order', error);
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

  navigateToAddOrder(): void {
    this.router.navigate(['order/add']);
  }

  navigateToDetailOrder(orderId: number): void {
    this.router.navigate(['order/detail/', orderId]);
  }

  navigateToEditOrder(orderId: number): void {
    this.router.navigate(['order/edit/', orderId]);
  }
}
