import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { VehiculeService } from '../../core/services/vehicule';
import { Vehicule } from '../../core/models/vehicule';
import { Role } from '../../core/models/role.enum';
import { ImportVehicules } from '../import-vehicules/import-vehicules';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, ImportVehicules],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  username: string | null = null;
  role: string | null = null;
  Role = Role;

  activeTab = signal<'inventaire' | 'import'>('inventaire');

  vehicules = signal<Vehicule[]>([]);
  isLoadingVehicules = signal(false);
  vehiculesError = signal('');
  searchTerm = signal('');
  statutFilter = signal<'TOUS' | 'EN_STOCK' | 'EN_VENTE' | 'VENDU'>('TOUS');

  filteredVehicules = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const statut = this.statutFilter();

    return this.vehicules().filter(v => {
      const matchStatut = statut === 'TOUS' || v.statut === statut;

      const matchSearch = term === '' ||
        v.immatriculation.toLowerCase().includes(term) ||
        v.marque.toLowerCase().includes(term) ||
        v.modele.toLowerCase().includes(term) ||
        (v.ville ?? '').toLowerCase().includes(term) ||
        (v.vin ?? '').toLowerCase().includes(term);

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

  setActiveTab(tab: 'inventaire' | 'import'): void {
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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}