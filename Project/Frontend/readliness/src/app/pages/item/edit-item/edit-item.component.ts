import { Component } from '@angular/core';
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
  selector: 'app-edit-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-item.component.html',
  styleUrl: './edit-item.component.scss',
})
export class EditItemComponent {
  itemResponse: ItemResponse | null = null;

  private readonly apiUrlUpdateItems =
    'http://localhost:8081/api/readliness/updateitem';

  private readonly apiUrlDetailItem =
    'http://localhost:8081/api/readliness/getdetailitem';

  updateItemObj: {
    itemsId: number;
    itemsName: string;
    itemsCode: string;
    stock: number;
    price: number;
  } = {
    itemsId: 0,
    itemsName: '',
    itemsCode: '',
    stock: 0,
    price: 0,
  };

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
          this.updateItemObj = {
            itemsId: id,
            itemsCode: data.itemsCode,
            itemsName: data.itemsName,
            stock: data.stock,
            price: data.price,
          };
        },
        error: (error) => {
          console.error('Error fetching item', error);
        },
      });
  }

  updateItem() {
    this.http.post(this.apiUrlUpdateItems, this.updateItemObj).subscribe({
      next: (response: any) => {
        console.log('Item Updated successfully', response);
        this.router.navigate(['/item/display']);
      },
      error: (error) => {
        console.error('Error updated item', error);
      },
    });
  }
}
