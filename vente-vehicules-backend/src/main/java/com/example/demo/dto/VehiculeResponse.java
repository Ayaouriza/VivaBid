package com.example.demo.dto;

import com.example.demo.entity.StatutVehicule;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeResponse {

    private Long id;
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
    private Double difExp;
    private String acheteur;
    private StatutVehicule statut;
    private String docsJustificatifsPath;
    private LocalDateTime dateCreation;
}