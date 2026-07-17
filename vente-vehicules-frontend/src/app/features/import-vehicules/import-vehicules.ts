import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VehiculeService } from '../../core/services/vehicule';
import { ImportResultat, LigneErreurImport } from '../../core/models/import-resultat';

@Component({
  selector: 'app-import-vehicules',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './import-vehicules.html',
  styleUrl: './import-vehicules.css',
})
export class ImportVehicules {

  selectedFile = signal<File | null>(null);
  isUploading = signal(false);
  resultat = signal<ImportResultat | null>(null);
  errorMessage = signal('');

  constructor(private vehiculeService: VehiculeService) {}

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile.set(input.files[0]);
      this.resultat.set(null);
      this.errorMessage.set('');
    }
  }

  onImport(): void {
    const file = this.selectedFile();
    if (!file) return;

    this.isUploading.set(true);
    this.errorMessage.set('');

    this.vehiculeService.importExcel(file).subscribe({
      next: (res) => {
        this.resultat.set(res);
        this.isUploading.set(false);
      },
      error: (err) => {
        this.errorMessage.set("Erreur lors de l'import. Vérifiez le format du fichier.");
        this.isUploading.set(false);
        console.error(err);
      }
    });
  }

  telechargerTemplate(): void {
    this.vehiculeService.telechargerTemplate().subscribe({
      next: (blob) => this.downloadBlob(blob, 'template_vehicules.xlsx'),
      error: (err) => console.error(err)
    });
  }

  telechargerErreurs(): void {
    const lignes: LigneErreurImport[] = this.resultat()?.lignesEnErreur ?? [];
    if (lignes.length === 0) return;

    this.vehiculeService.telechargerFichierErreurs(lignes).subscribe({
      next: (blob) => this.downloadBlob(blob, 'vehicules_erreurs.xlsx'),
      error: (err) => console.error(err)
    });
  }

  private downloadBlob(blob: Blob, filename: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    window.URL.revokeObjectURL(url);
  }

  resetImport(): void {
    this.selectedFile.set(null);
    this.resultat.set(null);
    this.errorMessage.set('');
  }
}