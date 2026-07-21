import { Injectable, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vente, VehiculeSimple } from '../models/vente';

@Injectable({
  providedIn: 'root'
})
export class VenteService {

  private readonly apiUrl = 'http://localhost:8080/api/ventes';

  // --- État temporaire de la vente en cours de création ---
  nouvelleDateVente = signal('');
  nouvelleHeureVente = signal('');
  vehiculesSelectionnes = signal<VehiculeSimple[]>([]);

  constructor(private http: HttpClient) {}

  // --- Appels API existants ---

  getAllVentes(): Observable<Vente[]> {
    return this.http.get<Vente[]>(this.apiUrl);
  }

  getVenteById(id: number): Observable<Vente> {
    return this.http.get<Vente>(`${this.apiUrl}/${id}`);
  }

  createVente(dateVente: string): Observable<Vente> {
    return this.http.post<Vente>(this.apiUrl, { dateVente });
  }

  getVehiculesDisponibles(dateDebut?: string, dateFin?: string, chargeAujourdhui?: boolean): Observable<VehiculeSimple[]> {
    let params = new HttpParams();
    if (chargeAujourdhui) {
      params = params.set('chargeAujourdhui', 'true');
    } else {
      if (dateDebut) params = params.set('dateDebut', dateDebut);
      if (dateFin) params = params.set('dateFin', dateFin);
    }
    return this.http.get<VehiculeSimple[]>(`${this.apiUrl}/vehicules-disponibles`, { params });
  }

  ajouterVehicule(venteId: number, vehiculeId: number): Observable<Vente> {
    return this.http.post<Vente>(`${this.apiUrl}/${venteId}/vehicules/${vehiculeId}`, {});
  }

  retirerVehicule(venteId: number, vehiculeId: number): Observable<Vente> {
    return this.http.delete<Vente>(`${this.apiUrl}/${venteId}/vehicules/${vehiculeId}`);
  }

  // --- Gestion de l'état "vente en cours de création" ---

  ajouterVehiculeSelection(vehicule: VehiculeSimple): void {
    this.vehiculesSelectionnes.update(list => {
      if (list.some(v => v.id === vehicule.id)) return list;
      return [...list, vehicule];
    });
  }

  retirerVehiculeSelection(vehiculeId: number): void {
    this.vehiculesSelectionnes.update(list => list.filter(v => v.id !== vehiculeId));
  }

  viderSelection(): void {
    this.vehiculesSelectionnes.set([]);
    this.nouvelleDateVente.set('');
    this.nouvelleHeureVente.set('');
  }
}