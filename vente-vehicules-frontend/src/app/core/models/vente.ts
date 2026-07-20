export interface VehiculeSimple {
  id: number;
  immatriculation: string;
  ville: string | null;
  marque: string;
  modele: string;
  prixExpert: number | null;
}

export interface Vente {
  id: number;
  dateVente: string;
  statut: 'PLANIFIEE' | 'EN_COURS' | 'TERMINEE';
  vehicules: VehiculeSimple[];
  dateCreation: string;
}