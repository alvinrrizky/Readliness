import { Component, VERSION } from '@angular/core';
import { FileUploadService } from './file-uploader.service';

@Component({
  selector: 'app-add-customer',
  standalone: true,
  imports: [],
  templateUrl: './add-customer.component.html',
  styleUrl: './add-customer.component.scss',
})
export class AddCustomerComponent {
  shortLink: string = '';
  loading: boolean = false;
  file: File | null = null;

  constructor(private fileUploadService: FileUploadService) {}

  onChange(event: Event) {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.file = target.files[0];
    }
  }

  onUpload() {
    if (this.file) {
      this.loading = true;
      console.log(this.file);
      this.fileUploadService.upload(this.file).subscribe({
        next: (event: any) => {
          if (typeof event === 'object') {
            this.shortLink = event.link;
            this.loading = false;
          }
        },
        error: (error) => {
          console.error('Upload error:', error);
          this.loading = false;
        },
      });
    }
  }
}
