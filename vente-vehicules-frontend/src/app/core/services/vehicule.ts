import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Vehicule } from '../models/vehicule';
import { ImportResultat, LigneErreurImport } from '../models/import-resultat';

@Injectable({
  providedIn: 'root'
})
export class VehiculeService {

  private readonly apiUrl = 'http://localhost:8080/api/vehicules';

  constructor(private http: HttpClient) {}

  getAllVehicules(): Observable<Vehicule[]> {
    return this.http.get<Vehicule[]>(this.apiUrl);
  }

  importExcel(file: File): Observable<ImportResultat> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ImportResultat>(`${this.apiUrl}/import`, formData);
  }

  telechargerFichierErreurs(lignesEnErreur: LigneErreurImport[]): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/import/erreurs`, lignesEnErreur, {
      responseType: 'blob'
    });
  }

  telechargerTemplate(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/import/template`, {
      responseType: 'blob'
    });
  }
}