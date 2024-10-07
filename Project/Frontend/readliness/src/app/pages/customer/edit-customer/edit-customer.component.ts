import { Component } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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
  selector: 'app-edit-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-customer.component.html',
  styleUrl: './edit-customer.component.scss',
})
export class EditCustomerComponent {
  private readonly apiUrlUpdateCustomer =
    'http://localhost:8081/api/readliness/updatecustomer';
  private readonly apiUrlDetailCustomer =
    'http://localhost:8081/api/readliness/getdetailcustomer';

  updateCustomerObj: {
    customerId: number;
    customerName: string;
    customerCode: string;
    isActive: number;
    lastOrderDate: Date;
    customerAddress: string;
    customerPhone: string;
    pic: string;
  } = {
    customerId: 0,
    customerName: '',
    customerCode: '',
    isActive: 0,
    lastOrderDate: new Date(),
    customerAddress: '',
    customerPhone: '',
    pic: '',
  };

  updateCustomerFile: {
    file: File | null;
  } = {
    file: null,
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
        this.fetchDetailCustomer(id);
      }
    });
  }

  fetchDetailCustomer(id: number): void {
    this.http
      .post<CustomerResponse>(this.apiUrlDetailCustomer + `?id=${id}`, {})
      .subscribe({
        next: (data) => {
          this.updateCustomerObj = {
            customerId: data.customerId,
            customerName: data.customerName,
            customerCode: data.customerCode,
            isActive: data.isActive,
            lastOrderDate: data.lastOrderDate,
            customerAddress: data.customerAddress,
            customerPhone: data.customerPhone,
            pic: data.pic,
          };
        },
        error: (error) => {
          console.error('Error fetching item', error);
        },
      });
  }

  updateCustomer() {
    const formData = new FormData();
    formData.append(
      'customerReq',
      new Blob([JSON.stringify(this.updateCustomerObj)], {
        type: 'application/json',
      })
    );

    if (this.updateCustomerFile.file) {
      formData.append(
        'file',
        this.updateCustomerFile.file,
        this.updateCustomerFile.file.name
      );
    }

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data');

    this.http.post(this.apiUrlUpdateCustomer, formData, { headers }).subscribe({
      next: (response: any) => {
        console.log('Customer updated successfully', response);
        this.router.navigate(['/customer/display']);
      },
      error: (error) => {
        console.error('Error update customer', error);
      },
    });
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.updateCustomerFile.file = file;
    }
  }
}
