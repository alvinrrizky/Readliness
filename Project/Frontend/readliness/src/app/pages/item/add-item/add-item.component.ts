import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-item',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-item.component.html',
  styleUrl: './add-item.component.scss',
})
export class AddItemComponent {
  private readonly apiUrlAddItems =
    'http://localhost:8081/api/readliness/saveitem';

  addItemObj: {
    itemsName: string;
    stock: number;
    price: number;
    isAvailable: number;
  } = {
    itemsName: '',
    stock: 0,
    price: 0,
    isAvailable: 0,
  };

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  addItem() {
    this.http.post(this.apiUrlAddItems, this.addItemObj).subscribe({
      next: (response: any) => {
        console.log('Item added successfully', response);
        this.router.navigate(['/item/display']);
      },
      error: (error) => {
        console.error('Error adding item', error);
      },
    });
  }
}
