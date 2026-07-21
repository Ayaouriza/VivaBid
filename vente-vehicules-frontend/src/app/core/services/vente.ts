import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vente, VehiculeSimple } from '../models/vente';

@Injectable({
  providedIn: 'root'
})
export class VenteService {

  private readonly apiUrl = 'http://localhost:8080/api/ventes';

  constructor(private http: HttpClient) {}

  getAllVentes(): Observable<Vente[]> {
    return this.http.get<Vente[]>(this.apiUrl);
  }

  getVenteById(id: number): Observable<Vente> {
    return this.http.get<Vente>(`${this.apiUrl}/${id}`);
  }

  createVente(dateVente: string): Observable<Vente> {
    return this.http.post<Vente>(this.apiUrl, { dateVente });
  }

  getVehiculesDisponibles(): Observable<VehiculeSimple[]> {
    return this.http.get<VehiculeSimple[]>(`${this.apiUrl}/vehicules-disponibles`);
  }

  ajouterVehicule(venteId: number, vehiculeId: number): Observable<Vente> {
    return this.http.post<Vente>(`${this.apiUrl}/${venteId}/vehicules/${vehiculeId}`, {});
  }

  retirerVehicule(venteId: number, vehiculeId: number): Observable<Vente> {
    return this.http.delete<Vente>(`${this.apiUrl}/${venteId}/vehicules/${vehiculeId}`);
  }
}