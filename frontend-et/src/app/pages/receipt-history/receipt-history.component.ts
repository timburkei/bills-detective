import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterLink} from "@angular/router";
import {UploadService} from "../../services/upload.service";
import {AuthService} from "@auth0/auth0-angular";
import {FormsModule} from "@angular/forms";

@Component({
  selector: 'app-receipt-history',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './receipt-history.component.html',
  styleUrl: './receipt-history.component.css'
})
export class ReceiptHistoryComponent implements OnInit{
  userId: string = null;
  uploads = null;
  fileError: string | null = null;
  uploadSuccessful: boolean = false;
  selectedFile: File | null = null;

  constructor(public auth: AuthService, private uploadService: UploadService) {}

  ngOnInit(): void {
    // Get user ID
    this.auth.user$.subscribe(
      (profile) => {
        this.userId = encodeURIComponent(profile.sub);

        //TODO: Get uploads
        this.uploadService.getUploads(this.userId).subscribe(
          data => {
            this.uploads = data;
            console.log("Uploads: ", this.uploads);
          },
          error => console.error("Error fetching uploads: ", error)
        );
      }
    );
  }

  onFileSelected(event: any): void {
    const fileInput = event.target;
    if (fileInput.files.length > 0) {
      const fileName = fileInput.files[0].name;

      if (fileName.toLowerCase().endsWith('.png') || fileName.toLowerCase().endsWith('.jpg') || fileName.toLowerCase().endsWith('.jpeg')) {
        this.fileError = null; // Setze Fehlermeldung zurück
        this.selectedFile = fileInput.files[0];
      } else {
        this.fileError = 'Bitte lade nur Dateien mit den Endungen .png, .jpg oder .jpeg hoch.';
        this.selectedFile = null;
      }
    }
  }

  protected uploadFile(): void {
    if (this.selectedFile) {
      this.uploadService.uploadFile(this.userId, this.selectedFile).subscribe(
        response => {
          this.uploadSuccessful = true;
          console.log('File successfully uploaded:', response);
        },
        error => {
          this.uploadSuccessful = false;
          console.error('Error uploading the file:', error);
        }
      );
    }
  }
}
