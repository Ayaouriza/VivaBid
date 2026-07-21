import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { VehiculeService } from '../../core/services/vehicule';
import { Vehicule } from '../../core/models/vehicule';
import { Role } from '../../core/models/role.enum';
import { ImportVehicules } from '../import-vehicules/import-vehicules';
import { VentesPlanifiees } from '../VentesPlanifiees/ventes-planifiees';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-dashboard',
  imports: [CommonModule,FormsModule, ImportVehicules, VentesPlanifiees],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  username: string | null = null;
  role: string | null = null;
  Role = Role;
  activeTab = signal<'inventaire' | 'import' | 'ventes'>('inventaire');
  
  vehicules = signal<Vehicule[]>([]);
  isLoadingVehicules = signal(false);
  vehiculesError = signal('');
  searchTerm = signal('');
  statutFilter = signal<'TOUS' | 'EN_STOCK' | 'NON_VENDU'|'EN_VENTE' | 'VENDU'>('TOUS');

  filteredVehicules = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const statut = this.statutFilter();

    return this.vehicules().filter(v => {
      const matchStatut = statut === 'TOUS' || v.statut === statut;

      const matchSearch = term === '' ||
        v.immatriculation.toLowerCase().includes(term) ||
        v.marqueModele.toLowerCase().includes(term) ||
        (v.contrat ?? '').toLowerCase().includes(term) ||
        (v.acheteur ?? '').toLowerCase().includes(term);

      return matchStatut && matchSearch;
    });
  });

  constructor(
    private authService: AuthService,
    private vehiculeService: VehiculeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.username = this.authService.getUsername();
    this.role = this.authService.getRole();

    if (this.role === Role.AGENT_SAISIE) {
      this.loadVehicules();
    }
  }

  setActiveTab(tab: 'inventaire' | 'import' | 'ventes'): void {
    this.activeTab.set(tab);
    if (tab === 'inventaire') {
      this.loadVehicules();
    }
  }

  loadVehicules(): void {
    this.isLoadingVehicules.set(true);
    this.vehiculeService.getAllVehicules().subscribe({
      next: (data) => {
        this.vehicules.set(data);
        this.isLoadingVehicules.set(false);
      },
      error: (err) => {
        this.vehiculesError.set('Impossible de charger les véhicules.');
        this.isLoadingVehicules.set(false);
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
  // --- Suppression ---
  vehiculeToDelete = signal<Vehicule | null>(null);
  isDeleting = signal(false);

  demanderSuppression(v: Vehicule): void {
    this.vehiculeToDelete.set(v);
  }

  annulerSuppression(): void {
    this.vehiculeToDelete.set(null);
  }

  confirmerSuppression(): void {
    const v = this.vehiculeToDelete();
    if (!v) return;

    this.isDeleting.set(true);
    this.vehiculeService.deleteVehicule(v.id).subscribe({
      next: () => {
        this.vehicules.set(this.vehicules().filter(x => x.id !== v.id));
        this.vehiculeToDelete.set(null);
        this.isDeleting.set(false);
      },
      error: (err) => {
        console.error(err);
        this.isDeleting.set(false);
        alert("Erreur lors de la suppression du véhicule.");
      }
    });
  }

  // --- Modification ---
  vehiculeToEdit = signal<Vehicule | null>(null);
  editForm: Partial<Vehicule> = {};
  isSaving = signal(false);
  editError = signal('');

  ouvrirModification(v: Vehicule): void {
    this.vehiculeToEdit.set(v);
    this.editForm = { ...v };
    this.editError.set('');
  }

  annulerModification(): void {
    this.vehiculeToEdit.set(null);
    this.editForm = {};
  }

  sauvegarderModification(): void {
    const v = this.vehiculeToEdit();
    if (!v) return;

    this.isSaving.set(true);
    this.editError.set('');

    this.vehiculeService.updateVehicule(v.id, this.editForm).subscribe({
      next: (updated) => {
        this.vehicules.set(
          this.vehicules().map(x => x.id === updated.id ? updated : x)
        );
        this.isSaving.set(false);
        this.vehiculeToEdit.set(null);
        this.editForm = {};
      },
      error: (err) => {
        console.error(err);
        this.isSaving.set(false);
        this.editError.set("Erreur lors de la modification du véhicule.");
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}