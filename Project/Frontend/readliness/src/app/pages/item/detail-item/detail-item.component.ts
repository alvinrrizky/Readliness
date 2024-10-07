import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface ItemResponse {
  itemsId: number;
  itemsName: string;
  itemsCode: string;
  stock: number;
  price: number;
  isAvailable: number;
  lastReStock: Date;
}

@Component({
  selector: 'app-detail-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './detail-item.component.html',
  styleUrl: './detail-item.component.scss',
})
export class DetailItemComponent implements OnInit {
  itemResponse: ItemResponse | null = null;

  private readonly apiUrlDetailItem =
    'http://localhost:8081/api/readliness/getdetailitem';

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      const id = +params['id'];
      if (id) {
        this.fetchDetailItem(id);
      }
    });
  }

  fetchDetailItem(id: number): void {
    this.http
      .post<ItemResponse>(this.apiUrlDetailItem + `?id=${id}`, {})
      .subscribe({
        next: (data) => {
          this.itemResponse = data;
        },
        error: (error) => {
          console.error('Error fetching item', error);
        },
      });
  }
}
