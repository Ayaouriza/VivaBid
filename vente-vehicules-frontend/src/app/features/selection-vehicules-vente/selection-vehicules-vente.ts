import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { VenteService } from '../../core/services/vente';
import { Vente } from '../../core/models/vente';
import { Vehicule } from '../../core/models/vehicule';
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

  vehiculesDisponibles = signal<Vehicule[]>([]);
  isLoading = signal(false);
  errorMessage = signal('');
  isValidating = signal(false);

  searchTerm = signal('');
  statutFilter = signal<'TOUS' | 'EN_STOCK' | 'NON_VENDU' | 'EN_VENTE' | 'VENDU'>('TOUS');
  chargeAujourdhui = signal(false);
  dateDebut = signal('');
  dateFin = signal('');

  // IDs des véhicules cochés, pas encore envoyés au backend
  selectionIds = signal<Set<number>>(new Set());

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

  nombreSelectionnes = computed(() => this.selectionIds().size);

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

  estDejaAjoute(vehiculeId: number): boolean {
    return this.vente()?.vehicules.some(v => v.id === vehiculeId) ?? false;
  }

  estCoche(vehiculeId: number): boolean {
    return this.selectionIds().has(vehiculeId);
  }

  toggleSelection(vehiculeId: number): void {
    if (this.estDejaAjoute(vehiculeId)) return;

    this.selectionIds.update(set => {
      const nouveauSet = new Set(set);
      if (nouveauSet.has(vehiculeId)) {
        nouveauSet.delete(vehiculeId);
      } else {
        nouveauSet.add(vehiculeId);
      }
      return nouveauSet;
    });
  }

  validerSelection(): void {
    const ids = Array.from(this.selectionIds());

    if (ids.length === 0) {
      this.router.navigate(['/dashboard'], { queryParams: { tab: 'ventes' } });
      return;
    }

    this.isValidating.set(true);

    const appels = ids.map(id => this.venteService.ajouterVehicule(this.venteId, id));

    forkJoin(appels).subscribe({
      next: () => {
        this.isValidating.set(false);
        this.router.navigate(['/dashboard'], { queryParams: { tab: 'ventes' } });
      },
      error: (err) => {
        console.error(err);
        this.isValidating.set(false);
        this.errorMessage.set("Erreur lors de l'ajout de certains véhicules.");
      }
    });
  }

  annuler(): void {
    this.router.navigate(['/dashboard'], { queryParams: { tab: 'ventes' } });
  }
}