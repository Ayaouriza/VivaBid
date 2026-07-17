import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { VehiculeService } from '../../core/services/vehicule';
import { Vehicule } from '../../core/models/vehicule';
import { Role } from '../../core/models/role.enum';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  username: string | null = null;
  role: string | null = null;
  Role = Role;

  vehicules = signal<Vehicule[]>([]);
  isLoadingVehicules = signal(false);
  vehiculesError = signal('');

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

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}