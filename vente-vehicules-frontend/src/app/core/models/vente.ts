import { Vehicule } from './vehicule';

export interface VehiculeSimple {
  id: number;
  immatriculation: string;
  marqueModele: string;
  prixExpert: number | null;
  statut: 'EN_STOCK' | 'EN_VENTE' | 'NON_VENDU' | 'VENDU';
  dateCreation: string;
}

export interface Vente {
  id: number;
  dateVente: string;
  statut: 'PLANIFIEE' | 'EN_COURS' | 'TERMINEE';
  vehicules: VehiculeSimple[];
  dateCreation: string;
}

export type VehiculeDisponible = Vehicule;