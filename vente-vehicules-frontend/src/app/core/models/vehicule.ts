export interface Vehicule {
  id: number;
  immatriculation: string;
  ville: string | null;
  marque: string;
  modele: string;
  anneeMec: number | null;
  vin: string | null;
  nombreCles: number | null;
  prixPlancher: number | null;
  statut: 'EN_STOCK' | 'EN_VENTE' | 'VENDU';
  carteGrisePath: string | null;
  docsJustificatifsPath: string | null;
  dateCreation: string;
}