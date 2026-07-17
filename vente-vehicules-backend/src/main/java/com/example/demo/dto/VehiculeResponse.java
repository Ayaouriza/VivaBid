package com.example.demo.dto;

import com.example.demo.entity.StatutVehicule;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VehiculeResponse {

    private Long id;
    private String immatriculation;
    private String ville;
    private String marque;
    private String modele;
    private Integer anneeMec;
    private String vin;
    private Integer nombreCles;
    private Double prixPlancher;
    private StatutVehicule statut;
    private String carteGrisePath;
    private String docsJustificatifsPath;
    private LocalDateTime dateCreation;
}