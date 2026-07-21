package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LigneErreurImport {

    private int numeroLigne;
    private String immatriculation;
    private String contrat;
    private String produit;
    private String situation;
    private String marqueModele;
    private String carburant;
    private String kilometrage;
    private String nombreCles;
    private String dateMec;
    private String dateRecuperation;
    private String dateVente;
    private String sejour;
    private String encImp;
    private String prixAchat;
    private String prixExpert;
    private String messageErreur;
}