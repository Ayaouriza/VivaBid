export interface Vehicule {
  id: number;
  immatriculation: string;
  contrat: string;
  produit: string;
  situation: string;
  marqueModele: string;
  carburant: string;
  kilometrage: string;
  nombreCles: number | null;
  dateMec: string | null;
  dateRecuperation: string | null;
  dateVente: string | null;
  sejour: number | null;
  encImp: number | null;
  prixAchat: number | null;
  prixExpert: number | null;
  vep: number | null;
  difExp: number | null;
  acheteur: string | null;
  statut: 'EN_STOCK' | 'EN_VENTE' | 'NON VENDU'|'VENDU';
  docsJustificatifsPath: string | null;
  dateCreation: string;
}