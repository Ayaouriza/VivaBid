import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VenteService } from '../../core/services/vente';
import { Vente, VehiculeSimple } from '../../core/models/vente';

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

  showPopup = signal(false);
  venteActive = signal<Vente | null>(null);
  vehiculesDisponibles = signal<VehiculeSimple[]>([]);
  isLoadingDisponibles = signal(false);

  constructor(private venteService: VenteService) {}

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

  ouvrirPopupAjout(vente: Vente): void {
    this.venteActive.set(vente);
    this.showPopup.set(true);
    this.isLoadingDisponibles.set(true);

    this.venteService.getVehiculesDisponibles().subscribe({
      next: (data) => {
        this.vehiculesDisponibles.set(data);
        this.isLoadingDisponibles.set(false);
      },
      error: (err) => {
        this.isLoadingDisponibles.set(false);
        console.error(err);
      }
    });
  }

  fermerPopup(): void {
    this.showPopup.set(false);
    this.venteActive.set(null);
  }

  ajouterVehicule(vehiculeId: number): void {
    const vente = this.venteActive();
    if (!vente) return;

    this.venteService.ajouterVehicule(vente.id, vehiculeId).subscribe({
      next: (venteMaj) => {
        this.venteActive.set(venteMaj);
        this.vehiculesDisponibles.update(list => list.filter(v => v.id !== vehiculeId));
        this.loadVentes();
      },
      error: (err) => console.error(err)
    });
  }

  retirerVehicule(vente: Vente, vehiculeId: number): void {
    this.venteService.retirerVehicule(vente.id, vehiculeId).subscribe({
      next: (venteMaj) => {
        if (this.venteActive()?.id === vente.id) {
          this.venteActive.set(venteMaj);
        }
        this.loadVentes();
      },
      error: (err) => console.error(err)
    });
  }
}