package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeRequest {

    private String immatriculation;
    private String contrat;
    private String produit;
    private String situation;
    private String marqueModele;
    private String carburant;
    private String kilometrage;
    private Integer nombreCles;
    private LocalDate dateMec;
    private LocalDate dateRecuperation;
    private LocalDate dateVente;
    private Integer sejour;
    private Double encImp;
    private Double prixAchat;
    private Double prixExpert;
    private Double vep;
    private String acheteur;
}