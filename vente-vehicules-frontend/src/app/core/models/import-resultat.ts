export interface LigneErreurImport {
  numeroLigne: number;
  immatriculation: string;
  ville: string;
  marque: string;
  modele: string;
  anneeMec: string;
  vin: string;
  nombreCles: string;
  prixPlancher: string;
  messageErreur: string;
}

export interface ImportResultat {
  totalLignes: number;
  nombreImportes: number;
  nombreErreurs: number;
  lignesEnErreur: LigneErreurImport[];
}
