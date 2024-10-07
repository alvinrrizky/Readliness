import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.scss',
})
export class AddCustomerComponent {
  private readonly apiUrlAddCustomer =
    'http://localhost:8081/api/readliness/savecustomer';

  addCustomerObj: {
    customerName: string;
    customerAddress: string;
    customerPhone: string;
    pic: string;
  } = {
    customerName: '',
    customerAddress: '',
    customerPhone: '',
    pic: '',
  };

  addCustomerFile: {
    file: File | null;
  } = {
    file: null,
  };

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {}

  addCustomer() {
    const formData = new FormData();
    formData.append(
      'customerReq',
      new Blob([JSON.stringify(this.addCustomerObj)], {
        type: 'application/json',
      })
    );

    if (this.addCustomerFile.file) {
      formData.append(
        'file',
        this.addCustomerFile.file,
        this.addCustomerFile.file.name
      );
    }

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');

    this.http.post(this.apiUrlAddCustomer, formData, { headers }).subscribe({
      next: (response: any) => {
        console.log('Customer added successfully', response);
        this.router.navigate(['/customer/display']);
      },
      error: (error) => {
        console.error('Error adding customer', error);
      },
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.addCustomerFile.file = file;
    }
  }
}
