import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { VenteService } from '../../core/services/vente';
import { Vente } from '../../core/models/vente';

@Component({
  selector: 'app-ventes-planifiees',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './ventes-planifiees.html',
  styleUrl: './ventes-planifiees.css',
})
export class VentesPlanifiees implements OnInit {

  ventes = signal<Vente[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');

  nouvelleDate = signal('');
  nouvelleHeure = signal('');
  isCreating = signal(false);

  constructor(
    private venteService: VenteService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadVentes();
  }

  loadVentes(): void {
    this.isLoading.set(true);
    this.venteService.getAllVentes().subscribe({
      next: (data) => {
        this.ventes.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Impossible de charger les ventes.');
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  onCreateVente(): void {
    const date = this.nouvelleDate();
    const heure = this.nouvelleHeure();
    if (!date || !heure) return;

    const dateVente = `${date}T${heure}:00`;

    this.isCreating.set(true);
    this.venteService.createVente(dateVente).subscribe({
      next: () => {
        this.isCreating.set(false);
        this.nouvelleDate.set('');
        this.nouvelleHeure.set('');
        this.loadVentes();
      },
      error: (err) => {
        this.isCreating.set(false);
        console.error(err);
      }
    });
  }

  allerAjouterVehicules(vente: Vente): void {
    this.router.navigate(['/ventes', vente.id, 'ajouter-vehicules']);
  }

  retirerVehicule(vente: Vente, vehiculeId: number): void {
    this.venteService.retirerVehicule(vente.id, vehiculeId).subscribe({
      next: () => {
        this.loadVentes();
      },
      error: (err) => console.error(err)
    });
  }
}