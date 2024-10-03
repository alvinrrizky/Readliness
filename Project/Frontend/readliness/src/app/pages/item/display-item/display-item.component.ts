import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

interface Item {
  itemsId: number;
  itemsName: string;
  itemsCode: string;
  stock: number;
  price: number;
  isAvailable: number;
  lastReStock: Date;
}

interface ItemResponse {
  itemList: Item[];
  pageCurrent: number;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
}

@Component({
  selector: 'app-display-item',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './display-item.component.html',
  styleUrl: './display-item.component.scss',
})
export class DisplayItemComponent {
  itemResponse: ItemResponse | null = null;
  private readonly apiUrlList =
    'http://localhost:8081/api/readliness/getitemlist';

  private readonly apiUrlDelete =
    'http://localhost:8081/api/readliness/deleteitem';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.fetchItems(1);
  }

  deleteItems(id: number): void {
    if (confirm('Are you sure you want to delete this item?')) {
      this.http
        .post<ItemResponse>(this.apiUrlDelete + `?id=${id}`, {})
        .subscribe({
          next: (response) => {
            console.log('Item deleted successfully', response);
            this.fetchItems(1);
          },
          error: (error) => {
            console.error('Error deleting item', error);
          },
          complete: () => {
            console.log('Delete operation completed');
          },
        });
    }
  }

  fetchItems(page: number): void {
    const requestBody = {
      page: page,
      shortBy: 'items_id',
      direction: 'asc',
      size: 7,
    };

    this.http.post<ItemResponse>(this.apiUrlList, requestBody).subscribe({
      next: (data) => {
        this.itemResponse = data;
      },
      error: (error) => {
        console.error('Error fetching items', error);
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

  navigateToAddItem(): void {
    this.router.navigate(['item/add']);
  }
}
