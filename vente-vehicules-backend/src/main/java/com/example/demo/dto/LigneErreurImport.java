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
    private String ville;
    private String marque;
    private String modele;
    private String anneeMec;
    private String vin;
    private String nombreCles;
    private String prixPlancher;
    private String messageErreur;
}