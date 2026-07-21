import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VenteService } from '../../core/services/vente';
import { Vente, VehiculeSimple } from '../../core/models/vente';

@Component({
  selector: 'app-selection-vehicules-vente',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './selection-vehicules-vente.html',
  styleUrl: './selection-vehicules-vente.css',
})
export class SelectionVehiculesVente implements OnInit {

  venteId!: number;
  vente = signal<Vente | null>(null);

  vehiculesDisponibles = signal<VehiculeSimple[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');

  searchTerm = signal('');
  statutFilter = signal<'TOUS' | 'EN_STOCK' | 'NON_VENDU' | 'EN_VENTE' | 'VENDU'>('TOUS');
  chargeAujourdhui = signal(false);
  dateDebut = signal('');
  dateFin = signal('');

  filteredVehicules = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const statut = this.statutFilter();

    return this.vehiculesDisponibles().filter(v => {
      const matchStatut = statut === 'TOUS' || v.statut === statut;

      const matchSearch = term === '' ||
        v.immatriculation.toLowerCase().includes(term) ||
        v.marqueModele.toLowerCase().includes(term);

      return matchStatut && matchSearch;
    });
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private venteService: VenteService
  ) {}

  ngOnInit(): void {
    this.venteId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadVente();
    this.loadVehiculesDisponibles();
  }

  loadVente(): void {
    this.venteService.getVenteById(this.venteId).subscribe({
      next: (data) => this.vente.set(data),
      error: (err) => console.error(err)
    });
  }

  loadVehiculesDisponibles(): void {
    this.isLoading.set(true);
    this.errorMessage.set('');

    const debut = this.chargeAujourdhui() ? undefined : (this.dateDebut() || undefined);
    const fin = this.chargeAujourdhui() ? undefined : (this.dateFin() || undefined);

    this.venteService.getVehiculesDisponibles(debut, fin, this.chargeAujourdhui()).subscribe({
      next: (data) => {
        this.vehiculesDisponibles.set(data);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.errorMessage.set('Impossible de charger les véhicules disponibles.');
        this.isLoading.set(false);
        console.error(err);
      }
    });
  }

  onSearchChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.searchTerm.set(input.value);
  }

  onStatutFilterChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.statutFilter.set(select.value as any);
  }

  toggleChargeAujourdhui(): void {
    this.chargeAujourdhui.update(v => !v);
    if (this.chargeAujourdhui()) {
      this.dateDebut.set('');
      this.dateFin.set('');
    }
    this.loadVehiculesDisponibles();
  }

  onDateDebutChange(value: string): void {
    this.dateDebut.set(value);
    this.chargeAujourdhui.set(false);
    this.loadVehiculesDisponibles();
  }

  onDateFinChange(value: string): void {
    this.dateFin.set(value);
    this.chargeAujourdhui.set(false);
    this.loadVehiculesDisponibles();
  }

  ajouterVehicule(vehiculeId: number): void {
    this.venteService.ajouterVehicule(this.venteId, vehiculeId).subscribe({
      next: (venteMaj) => {
        this.vente.set(venteMaj);
        this.vehiculesDisponibles.update(list => list.filter(v => v.id !== vehiculeId));
      },
      error: (err) => console.error(err)
    });
  }

  estDejaAjoute(vehiculeId: number): boolean {
    return this.vente()?.vehicules.some(v => v.id === vehiculeId) ?? false;
  }

 validerSelection(): void {
  this.router.navigate(['/dashboard'], { queryParams: { tab: 'ventes' } });
}
}